package mesosphere.chaos

import mesosphere.chaos.http.{HttpConf, HttpModule}
import mesosphere.chaos.metrics.MetricsModule

/**
  * Wraps up the metrics and http modules.
  * @param conf configuration used to set up the http server.
  */
class ChaosModule(conf: HttpConf) {
  lazy val metricsModule = new MetricsModule
  lazy val httpModule = new HttpModule(conf, metricsModule.registry)
}
