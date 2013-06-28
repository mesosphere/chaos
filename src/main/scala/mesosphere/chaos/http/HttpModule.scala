package mesosphere.chaos.http

import com.google.inject._
import org.eclipse.jetty.server.{RequestLog, Server}
import org.rogach.scallop._
import org.eclipse.jetty.servlet.{DefaultServlet, ServletContextHandler}
import com.google.inject.servlet.GuiceFilter
import java.util
import javax.servlet.DispatcherType
import scala.{Array, Some}
import com.codahale.metrics.jetty9.InstrumentedHandler
import org.eclipse.jetty.server.handler.{RequestLogHandler, HandlerCollection}

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
    bind(classOf[RequestLog]).to(classOf[ChaosRequestLog])
  }

  @Provides
  def provideHttpServer(handlers: HandlerCollection) = {
    val server = new Server(conf.port())
    server.setHandler(handlers)
    server
  }

  @Provides
  def provideHandlerCollection(instrumentedHandler: InstrumentedHandler,
                               logHandler: RequestLogHandler): HandlerCollection = {
    val handlers = new HandlerCollection()
    handlers.setHandlers(Array(instrumentedHandler, logHandler))
    handlers
  }

  @Provides
  def provideRequestLogHandler(requestLog: RequestLog) = {
    val handler = new RequestLogHandler()
    handler.setRequestLog(requestLog)
    handler
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
