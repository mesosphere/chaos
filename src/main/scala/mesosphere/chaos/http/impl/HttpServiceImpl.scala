package mesosphere.chaos.http.impl

import java.util
import javax.servlet.{Filter, Servlet, DispatcherType}

import com.sun.jersey.api.core.DefaultResourceConfig
import com.sun.jersey.spi.container.servlet.ServletContainer
import org.eclipse.jetty.servlet.{FilterHolder, ServletHolder}
import org.slf4j.LoggerFactory
import mesosphere.chaos.http.HttpService

import scala.util.Try
/**
  * Wrapper for starting and stopping the HttpServer.
  */
class HttpServiceImpl(httpServer: HttpServer) extends HttpService {
  val servletPath = "/*"

  private[this] val log = LoggerFactory.getLogger(getClass.getName)
  private[this] var started = false

  def startUp() {
    log.debug("Starting up HttpServer.")
    try {
      started = true
      preStartUp()
      httpServer.start()
    }
    catch {
      case e: Exception =>
        log.error("Failed to start HTTP service", e)
        Try(httpServer.stop())
        throw e
    }
  }

  def shutDown() {
    log.debug("Shutting down HttpServer.")
    httpServer.stop()
  }

  def preStartUp() = {
    val config = new DefaultResourceConfig()
    val singletons = config.getSingletons
    // Add for JSON parsing
    singletons.add(new JsonProvider)
    // Add the rest of resources
    resources.foreach(singletons.add)
    httpServer.servletHandler.addServlet(new ServletHolder(new ServletContainer(config)), servletPath)
  }

  override def registerServlet(servlet: Servlet, path: String) = throwIfServerStarted(() =>
    httpServer.servletHandler.addServlet(new ServletHolder(servlet), path))

  override def registerFilter(filter: Filter, path: String) = throwIfServerStarted(() =>
    httpServer.servletHandler.addFilter(new FilterHolder(filter), path, util.EnumSet.allOf(classOf[DispatcherType])))

  override def getRegisteredServlets: Seq[Servlet] = httpServer.servletHandler.getServletHandler.getServlets.map(_.getServlet)

  override def getRegisteredFilter: Seq[Filter] = httpServer.servletHandler.getServletHandler.getFilters.map(_.getFilter)

  private[this] var resources = Seq.empty[AnyRef]
  override def registerResources(additionalResources: AnyRef*) = throwIfServerStarted(() =>
    resources = resources ++ additionalResources)

  override def getRegisteredResources: Seq[AnyRef] = resources

  case class ServerConfigurationException(msg: String) extends Exception(msg)

  def throwIfServerStarted(fn: () => Unit) = {
    if(started) throw new ServerConfigurationException("Server has already started. No additional resource, servlet or filter can be added anymore.")
    else fn()
  }
}
