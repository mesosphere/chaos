package mesosphere.chaos.metrics

import com.google.inject.AbstractModule
import com.codahale.metrics.MetricRegistry

/**
 * @author Tobi Knaup
 */

class MetricsModule extends AbstractModule {
  def configure() {
    bind(classOf[MetricRegistry]).asEagerSingleton()
  }
}
