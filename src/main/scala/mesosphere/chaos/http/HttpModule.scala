package mesosphere.chaos.http

import com.codahale.metrics.jetty8.InstrumentedHandler
import com.google.inject._
import com.google.inject.servlet.GuiceFilter
import java.io.File
import java.net.InetSocketAddress
import java.util
import org.apache.log4j.Logger
import javax.servlet.DispatcherType
import org.eclipse.jetty.security._
import org.eclipse.jetty.security.authentication.BasicAuthenticator
import org.eclipse.jetty.server._
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.server.handler.{ RequestLogHandler, HandlerCollection }
import org.eclipse.jetty.servlet.{ DefaultServlet, ServletContextHandler }
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.eclipse.jetty.util.security.{ Password, Constraint }
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector

class HttpModule(conf: HttpConf) extends AbstractModule {

  // TODO make configurable
  val welcomeFiles = Array("index.html")
  private[this] val log = Logger.getLogger(getClass.getName)

  protected val resourceCacheControlHeader: Option[String] = None

  def configure() {
    bind(classOf[HttpService])
    bind(classOf[GuiceServletConfig]).asEagerSingleton()
    bind(classOf[RequestLog]).to(classOf[ChaosRequestLog])
  }

  def getSSLConnector(server: Server): Option[Connector] = {
    for {
      keystorePath <- conf.sslKeystorePath.get
      keystorePassword <- conf.sslKeystorePassword.get
      connector = createSSLConnector(keystorePath, keystorePassword)
    } yield connector
  }

  def createSSLConnector(keystorePath: String, keystorePassword: String): SslSelectChannelConnector = {
    val keystore = new File(keystorePath)
    require(keystore.exists() && keystore.canRead,
      f"${conf.sslKeystorePath()} is invalid or not readable!")

    val contextFactory = new SslContextFactory()
    contextFactory.setKeyStorePath(keystorePath)
    contextFactory.setKeyStorePassword(keystorePassword)

    val sslConnector = new SslSelectChannelConnector(contextFactory)
    sslConnector.setPort(conf.httpsPort())

    sslConnector
  }

  @Provides
  @Singleton
  def provideHttpServer(handlers: HandlerCollection) = {

    val socketAddress =
      if (conf.httpAddress.isSupplied)
        new InetSocketAddress(conf.httpAddress(), conf.httpPort())
      else
        new InetSocketAddress(conf.httpPort())

    val server = new Server(socketAddress)
    server.setHandler(handlers)
    val sslConnector = getSSLConnector(server)
    if (sslConnector.nonEmpty) {
      log.info("Adding SSL support.")
      server.addConnector(sslConnector.get)
    }
    else {
      log.warn("No SSL support configured.")
    }
    server
  }

  @Provides
  @Singleton
  def provideHandlerCollection(instrumentedHandler: InstrumentedHandler,
                               logHandler: RequestLogHandler,
                               resourceHandler: ResourceHandler): HandlerCollection = {
    val handlers = new HandlerCollection()
    handlers.setHandlers(Array(resourceHandler, instrumentedHandler, logHandler))
    handlers
  }

  @Provides
  @Singleton
  def provideRequestLogHandler(requestLog: RequestLog) = {
    val handler = new RequestLogHandler()
    handler.setRequestLog(requestLog)
    handler
  }

  @Provides
  @Singleton
  def provideResourceHandler() = {
    val handler = new ResourceHandler
    handler.setDirectoriesListed(false)
    resourceCacheControlHeader foreach handler.setCacheControl
    handler.setWelcomeFiles(welcomeFiles)
    handler.setResourceBase(conf.assetsUrl().toExternalForm)
    handler.setAliases(true) // Enables use of relative paths
    handler
  }

  @Provides
  @Singleton
  def provideHandler(guiceServletConf: GuiceServletConfig): ServletContextHandler = {
    val handler = new ServletContextHandler()
    // Filters don't run if no servlets are bound, so we bind the DefaultServlet
    handler.addServlet(classOf[DefaultServlet], "/*")
    handler.addFilter(classOf[GuiceFilter], "/*", util.EnumSet.allOf(classOf[DispatcherType]))
    handler.addEventListener(guiceServletConf)

    conf.httpCredentials.get flatMap createSecurityHandler foreach handler.setSecurityHandler
    handler
  }

  def createSecurityHandler(httpCredentials: String): Option[ConstraintSecurityHandler] = {

    val credentialsPattern = "(.+):(.+)".r

    httpCredentials match {
      case credentialsPattern(userName, password) =>
        Option(createSecurityHandler(userName, password))
      case _ =>
        log.error(s"The HTTP credentials must be specified in the form of 'user:password'.")
        None
    }
  }

  def createSecurityHandler(userName: String, password: String): ConstraintSecurityHandler = {

    val constraint = new Constraint(Constraint.__BASIC_AUTH, "user")
    constraint.setAuthenticate(true)

    //TODO(FL): Make configurable
    constraint.setRoles(Array("user", "admin"))

    // map the security constraint to the root path.
    val cm = new ConstraintMapping()
    cm.setConstraint(constraint)
    cm.setPathSpec("/*")

    // create the security handler, set the authentication to Basic
    // and assign the realm.
    val csh = new ConstraintSecurityHandler()
    csh.setAuthenticator(new BasicAuthenticator())
    csh.setRealmName(conf.httpCredentialsRealm())
    csh.addConstraintMapping(cm)
    csh.setLoginService(createLoginService(userName, password))

    csh
  }

  def createLoginService(userName: String, password: String): LoginService = {

    val loginService = new MappedLoginService() {
      override def loadUser(username: String): UserIdentity = null
      override def loadUsers(): Unit = {}
      override def getName: String = conf.httpCredentialsRealm()
    }

    //TODO(*): Use a MD5 instead.
    loginService.putUser(userName, new Password(password), Array("user"))
    loginService
  }

}

