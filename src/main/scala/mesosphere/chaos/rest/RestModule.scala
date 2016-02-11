package mesosphere.chaos.rest

import javax.servlet.http.HttpServlet

import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.servlets.MetricsServlet
import mesosphere.chaos.http.HttpService

import mesosphere.chaos.rest.impl.{LogConfigServlet, ServiceStatusServlet, PingServlet}

/**
  * Created by alex on 21/01/16.
  */
class RestModule(httpService: HttpService, metricRegistry: MetricRegistry) {
  // Override these in a subclass to mount resources at a different path
  val pingUrl = "/ping"
  val metricsUrl = "/metrics"
  val loggingUrl = "/logging"
  val statusUrl = "/status"
  val statusCatchAllUrl = "/status/*"

  // Initializing servlets
  lazy val pingServlet: HttpServlet = new PingServlet
  lazy val metricsServlet: HttpServlet = new MetricsServlet(metricRegistry)
  lazy val logConfigServlet: HttpServlet = new LogConfigServlet
  lazy val serviceStatusServlet = new ServiceStatusServlet(new ServiceStatus())

  // Binding servlets
  httpService.registerServlet(pingServlet, pingUrl)
  httpService.registerServlet(metricsServlet, metricsUrl)
  httpService.registerServlet(logConfigServlet, loggingUrl)
  httpService.registerServlet(serviceStatusServlet, statusUrl)
  httpService.registerServlet(serviceStatusServlet, statusCatchAllUrl)
}
