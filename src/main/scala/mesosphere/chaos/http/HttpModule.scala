package mesosphere.chaos.http

import com.codahale.metrics.MetricRegistry
import mesosphere.chaos.http.impl.{ HttpServer, HttpServiceImpl }
import com.softwaremill.macwire._

/**
  * Provides a http service, which in it's turn contains a Jetty http server.
  * @param httpConf configuration needed in order to set up the Jetty server.
  */
@Module
class HttpModule(httpConf: HttpConf, metricsRegistry: MetricRegistry) {
  lazy val httpServer = wire[HttpServer]
  lazy val httpService = wire[HttpServiceImpl]
}
