package mesosphere.chaos.metrics

import java.lang.management.ManagementFactory

import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.jersey.InstrumentedResourceMethodDispatchAdapter
import com.codahale.metrics.jetty9.InstrumentedHandler
import com.codahale.metrics.jvm.{ BufferPoolMetricSet, GarbageCollectorMetricSet, MemoryUsageGaugeSet, ThreadStatesGaugeSet }
import mesosphere.chaos.http.HttpConf
import org.eclipse.jetty.security.authentication.BasicAuthenticator
import org.eclipse.jetty.security.{ConstraintMapping, ConstraintSecurityHandler, MappedLoginService, LoginService}
import org.eclipse.jetty.server.UserIdentity
import org.eclipse.jetty.servlet.{DefaultServlet, ServletContextHandler}
import org.eclipse.jetty.util.security.{Constraint, Password}
import org.slf4j.LoggerFactory

class MetricsModule(conf: HttpConf) {
  private[this] val log = LoggerFactory.getLogger(getClass.getName)
  lazy val registry: MetricRegistry = {
    val registry = new MetricRegistry
    registry.register("jvm.gc", new GarbageCollectorMetricSet())
    registry.register("jvm.buffers", new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer))
    registry.register("jvm.memory", new MemoryUsageGaugeSet())
    registry.register("jvm.threads", new ThreadStatesGaugeSet())
    registry
  }

  lazy val provideInstrumentedResourceMethodDispatchAdapter: InstrumentedResourceMethodDispatchAdapter = new InstrumentedResourceMethodDispatchAdapter(registry)
}
