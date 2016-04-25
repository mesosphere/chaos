package mesosphere.chaos.rest

import javax.servlet.http.HttpServlet

import com.codahale.metrics.servlets.MetricsServlet
import mesosphere.chaos.ChaosModule
import mesosphere.chaos.rest.impl.{ LogConfigServlet, PingServlet }
import com.softwaremill.macwire._

/**
  * Registers a couple of default servlets to the Jetty server. Those are:
  *  - ping servlet. Replies to a GET by printing "pong".
  *  - metrics servlet. Lists all metrics included in the metrics registry.
  *  - logging servlet. Allows dynamic adjustments of http configuration.
  * @param chaosModule module including the metrics and http modules.
  */
@Module
class RestModule(chaosModule: ChaosModule) {
  // Override these in a subclass to mount resources at a different path
  val pingUrl = "/ping"
  val metricsUrl = "/metrics"
  val loggingUrl = "/logging"

  // Initializing servlets
  lazy val pingServlet: HttpServlet = wire[PingServlet]
  lazy val registry = chaosModule.metricsModule.registry
  lazy val metricsServlet: HttpServlet = new MetricsServlet(registry)
  lazy val logConfigServlet: HttpServlet = wire[LogConfigServlet]

  // Binding servlets
  val httpService = chaosModule.httpModule.httpService
  httpService.registerServlet(pingServlet, pingUrl)
  httpService.registerServlet(metricsServlet, metricsUrl)
  httpService.registerServlet(logConfigServlet, loggingUrl)
}
