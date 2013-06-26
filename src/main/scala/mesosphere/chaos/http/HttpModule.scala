package mesosphere.chaos.http

import com.google.inject.{Injector, Provides, AbstractModule}
import org.eclipse.jetty.server.{Handler, Server}
import org.rogach.scallop._
import org.eclipse.jetty.servlet.{DefaultServlet, ServletContextHandler}
import com.google.inject.servlet.{GuiceServletContextListener, GuiceFilter}
import java.util
import javax.servlet.DispatcherType

/**
 * @author Florian Leibert (flo@leibert.de)
 */
trait HttpConf extends ScallopConf {
  lazy val port = opt[Int]("http_port", descr = "The port to listen on for HTTP requests", default = Some(8080), noshort = true)
}

class HttpModule(conf : HttpConf) extends AbstractModule {
  def configure() {
    bind(classOf[HttpService])
  }

  @Provides
  def provideHttpServer(handler: Handler) = {
    val server = new Server(conf.port())
    server.setHandler(handler)
    server
  }

  @Provides
  def provideHandler() : Handler = {
    val handler = new ServletContextHandler()
    // Filters don't run if no servlets are bound, so we bind the DefaultServlet
    handler.addServlet(classOf[DefaultServlet], "/*")
    handler.addFilter(classOf[GuiceFilter], "/*", util.EnumSet.allOf(classOf[DispatcherType]))
    handler
  }
}
