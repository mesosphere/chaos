package mesosphere.chaos.metrics

import com.google.inject.{Singleton, Provides, AbstractModule}
import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.jersey.InstrumentedResourceMethodDispatchAdapter

/**
 * @author Tobi Knaup
 */

class MetricsModule extends AbstractModule {

  def configure() {
    bind(classOf[MetricRegistry]).asEagerSingleton()
  }

  @Singleton
  @Provides
  def provideInstrumentedResourceMethodDispatchAdapter(registry: MetricRegistry) =
    new InstrumentedResourceMethodDispatchAdapter(registry)

}
