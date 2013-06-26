package mesosphere.chaos.http

import com.google.inject._
import org.eclipse.jetty.server.Server
import org.rogach.scallop._
import org.eclipse.jetty.servlet.{DefaultServlet, ServletContextHandler}
import com.google.inject.servlet.GuiceFilter
import java.util
import javax.servlet.DispatcherType
import scala.Some

/**
 * @author Florian Leibert (flo@leibert.de)
 */
trait HttpConf extends ScallopConf {
  lazy val port = opt[Int]("http_port", descr = "The port to listen on for HTTP requests", default = Some(8080), noshort = true)
}

class HttpModule(conf: HttpConf) extends AbstractModule {
  def configure() {
    bind(classOf[HttpService])
    bind(classOf[GuiceServletConfig]).asEagerSingleton()
  }

  @Provides
  def provideHttpServer(handler: ServletContextHandler) = {
    val server = new Server(conf.port())
    server.setHandler(handler)
    server
  }

  @Singleton
  @Provides
  def provideHandler(guiceServletConf: GuiceServletConfig): ServletContextHandler = {
    val handler = new ServletContextHandler()
    // Filters don't run if no servlets are bound, so we bind the DefaultServlet
    handler.addServlet(classOf[DefaultServlet], "/*")
    handler.addFilter(classOf[GuiceFilter], "/*", util.EnumSet.allOf(classOf[DispatcherType]))
    handler.addEventListener(guiceServletConf)
    handler
  }
}
