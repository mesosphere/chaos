package mesosphere.chaos.http

import java.io.File
import java.util.EnumSet
import javax.servlet.DispatcherType
import scala.Array
import scala.Some

import com.codahale.metrics.jetty8.InstrumentedHandler
import com.google.inject._
import com.google.inject.servlet.GuiceFilter
import org.apache.log4j.Logger
import org.apache.shiro.web.env.EnvironmentLoaderListener
import org.apache.shiro.web.servlet.ShiroFilter
import org.eclipse.jetty.security._
import org.eclipse.jetty.security.authentication.BasicAuthenticator
import org.eclipse.jetty.server._
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.server.handler.{ RequestLogHandler, HandlerCollection }
import org.eclipse.jetty.server.session.SessionHandler
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector
import org.eclipse.jetty.servlet.{ DefaultServlet, ServletContextHandler }
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.eclipse.jetty.util.security.{ Password, Constraint }

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
    if (conf.sslKeystorePath.isSupplied) {
      val keystore = new File(conf.sslKeystorePath())
      require(keystore.exists() && keystore.canRead,
        f"${conf.sslKeystorePath()} is invalid or not readable!")
      val contextFactory = new SslContextFactory()
      contextFactory.setKeyStorePath(conf.sslKeystorePath())
      contextFactory.setKeyStorePassword(conf.sslKeystorePassword())
      val sslConnector = new SslSelectChannelConnector(contextFactory)
      sslConnector.setPort(conf.httpsPort())
      return Some(sslConnector)
    }
    None
  }

  @Provides
  @Singleton
  def provideHttpServer(handlers: HandlerCollection) = {
    val server = new Server(conf.httpPort())
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
    val handler = new ServletContextHandler(ServletContextHandler.SESSIONS)
    handler.setSessionHandler(new SessionHandler())

    // Filters don't run if no servlets are bound, so we bind the DefaultServlet
    handler.addServlet(classOf[DefaultServlet], "/*")

    if (conf.shiroIni.isSupplied) {
      // The ShiroFilter must be added before the GuiceFilter
      // The GuiceFilter sets up the Shiro authorization code and when the rest endpoint is intercepted it expects
      // that the authentication postion done by the ShiroFilter has already happened
      handler.addFilter(classOf[ShiroFilter], "/*",
        EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ERROR));
      handler.setInitParameter("shiroConfigLocations", conf.shiroIni());
      handler.addEventListener(new EnvironmentLoaderListener)
    }

    handler.addFilter(classOf[GuiceFilter], "/*", EnumSet.allOf(classOf[DispatcherType]))
    handler.addEventListener(guiceServletConf)

    if (conf.httpCredentials.isSupplied) {
      handler.setSecurityHandler(getLegacySecurityHandler())
    }

    handler
  }

  def getLegacySecurityHandler(): ConstraintSecurityHandler = {
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
    csh.setRealmName("chaos-realm")
    csh.addConstraintMapping(cm);

    val loginService = new KeyValueLoginService
    require(conf.httpCredentials().contains(":"),
      f"http_credentials '${conf.httpCredentials()}' " +
        f"must contain a ':' to separate user and password.")
    val tup = conf.httpCredentials().split(":", 2)

    //TODO(*): Use a MD5 instead.
    loginService.putUser(tup(0), new Password(tup(1)), Array("user"))

    csh.setLoginService(loginService)
    csh
  }
}

//TODO(*): Allow alternative loading from file.
class KeyValueLoginService()
    extends MappedLoginService {

  def loadUser(username: String): UserIdentity = {
    return null
  }

  def loadUsers {
  }

}
