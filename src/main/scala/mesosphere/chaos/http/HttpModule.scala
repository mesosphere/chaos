package mesosphere.chaos.http

import com.google.inject._
import org.eclipse.jetty.server._
import org.rogach.scallop._
import org.eclipse.jetty.servlet.{DefaultServlet, ServletContextHandler}
import com.google.inject.servlet.GuiceFilter
import java.util
import javax.servlet.DispatcherType
import scala.{Array, Some}
import com.codahale.metrics.jetty8.InstrumentedHandler
import org.eclipse.jetty.server.handler.{ResourceHandler, RequestLogHandler, HandlerCollection}
import com.codahale.metrics.jetty9.InstrumentedHandler
import org.eclipse.jetty.server.handler.{RequestLogHandler, HandlerCollection}
import org.eclipse.jetty.util.ssl.SslContextFactory
import java.io.File
import javax.inject.Named
import scala.Some
import java.util.logging.Logger
import org.eclipse.jetty.util.security.{Password, Credential, Constraint}
import org.eclipse.jetty.security._
import org.eclipse.jetty.security.authentication.BasicAuthenticator
import scala.Some

/**
 * @author Florian Leibert (flo@leibert.de)
 */
trait HttpConf extends ScallopConf {
  lazy val port = opt[Int]("http_port",
    descr = "The port to listen on for HTTP requests", default = Some(8080),
    noshort = true)

  lazy val httpsPort = opt[Int]("https_port",
    descr = "The port to listen on for HTTPS requests", default = Some(8443),
    noshort = true)

  lazy val sslKeystorePath = opt[String]("ssl_keystore_path",
    descr = "Provides the keystore, if supplied, SSL is enabled",
    default = None, noshort = true)

  lazy val sslKeystorePassword =  opt[String]("ssl_keystore_password",
    descr = "The password for the keystore", default = None, noshort = true)

  lazy val httpCredentials =  opt[String]("http_credentials",
    descr = "Credentials for accessing the http service." +
      "If empty, anyone can access the HTTP endpoint. A username:password" +
      "is expected where the username must not contain ':'",
    default = None, noshort = true)

}

class HttpModule(conf: HttpConf) extends AbstractModule {

  // TODO make configurable
  val welcomeFiles = Array("index.html")
  val resourceBase = getClass.getClassLoader.getResource("assets").toExternalForm
  private[this] val log = Logger.getLogger(getClass.getName)

  def configure() {
    bind(classOf[HttpService])
    bind(classOf[GuiceServletConfig]).asEagerSingleton()
    bind(classOf[RequestLog]).to(classOf[ChaosRequestLog])
  }

  def getSSLConnector(server: Server): Option[ServerConnector] = {
    if (conf.sslKeystorePath.isSupplied) {
      val keystore = new File(conf.sslKeystorePath())
      require(keystore.exists() && keystore.canRead,
        f"${conf.sslKeystorePath()} is invalid or not readable!")
      val contextFactory = new SslContextFactory()
      contextFactory.setKeyStorePath(conf.sslKeystorePath())
      contextFactory.setKeyStorePassword(conf.sslKeystorePassword())

      val httpsConfig = new HttpConfiguration()
      //TODO(*): Make configurable.
      httpsConfig.setSecureScheme("https")
      httpsConfig.setSecurePort(conf.httpsPort())
      httpsConfig.setOutputBufferSize(32768)
      httpsConfig.setRequestHeaderSize(8192)
      httpsConfig.setResponseHeaderSize(8192)
      httpsConfig.setSendServerVersion(true)
      httpsConfig.setSendDateHeader(false)
      httpsConfig.addCustomizer(new SecureRequestCustomizer())
      val sslConnector = new ServerConnector(server,
        new SslConnectionFactory(contextFactory, "http/1.1"),
        new HttpConnectionFactory(httpsConfig))
      sslConnector.setPort(conf.httpsPort())
      return Some(sslConnector)
    }
    None
  }

  @Provides
  @Singleton
  def provideHttpServer(handlers: HandlerCollection) = {
    val server = new Server(conf.port())
    server.setHandler(handlers)
    val sslConnector = getSSLConnector(server)
    if (sslConnector.nonEmpty) {
      log.info("Adding SSL support.")
      server.addConnector(sslConnector.get)
    } else {
      log.warning("No SSL support configured.")
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
    handler.setWelcomeFiles(welcomeFiles)
    handler.setResourceBase(resourceBase)
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
    if (conf.httpCredentials.isSupplied) {
      handler.setSecurityHandler(getSecurityHandler())
    }
    handler
  }

  def getSecurityHandler(): ConstraintSecurityHandler = {
    val constraint = new Constraint(Constraint.__BASIC_AUTH,"user");
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
