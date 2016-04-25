package mesosphere.chaos.http

import mesosphere.chaos.http.impl.{ HttpServer, HttpServiceImpl }
import com.softwaremill.macwire._
import mesosphere.chaos.metrics.MetricsModule

/**
  * Provides a http service, which in it's turn contains a Jetty http server.
  */
@Module
trait HttpModule extends MetricsModule {
  // dependency: configuration needed in order to set up the Jetty server.
  def httpConf: HttpConf

  lazy val httpServer = wire[HttpServer]
  lazy val httpService = wire[HttpServiceImpl]
}
