package mesosphere.chaos.metrics

import com.google.inject.{ Singleton, Provides, AbstractModule }
import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.jersey.InstrumentedResourceMethodDispatchAdapter
import com.codahale.metrics.jetty8.InstrumentedHandler
import org.eclipse.jetty.servlet.ServletContextHandler

class MetricsModule extends AbstractModule {

  def configure() {
    bind(classOf[MetricRegistry]).asEagerSingleton()
  }

  @Singleton
  @Provides
  def provideInstrumentedResourceMethodDispatchAdapter(registry: MetricRegistry) =
    new InstrumentedResourceMethodDispatchAdapter(registry)

  @Singleton
  @Provides
  def provideInstrumentedHandler(servletHandler: ServletContextHandler, registry: MetricRegistry) = {
    new InstrumentedHandler(registry, servletHandler)
  }

}
