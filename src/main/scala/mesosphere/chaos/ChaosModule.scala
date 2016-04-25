package mesosphere.chaos

import com.softwaremill.macwire.Module
import mesosphere.chaos.http.HttpModule
import mesosphere.chaos.metrics.MetricsModule

/**
  * Wraps up the metrics and http modules.
  */
@Module
trait ChaosModule extends MetricsModule with HttpModule
