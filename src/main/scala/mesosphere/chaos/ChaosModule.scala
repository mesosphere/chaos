package mesosphere.chaos

import mesosphere.chaos.http.{ HttpConf, HttpModule }
import mesosphere.chaos.metrics.MetricsModule
import com.softwaremill.macwire._

/**
  * Wraps up the metrics and http modules.
  * @param conf configuration used to set up the http server.
  */
@Module
class ChaosModule(conf: HttpConf) {
  lazy val httpModule = wire[HttpModule]
  lazy val metricsModule = wire[MetricsModule]
  // Needed for http module.
  lazy val metricsRegistry = metricsModule.registry
}
