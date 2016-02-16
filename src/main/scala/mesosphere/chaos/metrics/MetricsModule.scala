package mesosphere.chaos.metrics

import java.lang.management.ManagementFactory

import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.jvm.{ BufferPoolMetricSet, GarbageCollectorMetricSet, MemoryUsageGaugeSet, ThreadStatesGaugeSet }
import org.slf4j.LoggerFactory

/**
  * Includes a registry of metric instances. Those contain:
  *  - a set of gauges for the counts and elapsed times of garbage collections.
  *  - a set of gauges for the count, usage, and capacity of the JVM's direct and mapped buffer pools.
  *  - a set of gauges for JVM memory usage, including stats on heap vs. non-heap memory, plus
  *    GC-specific memory pools.
  *  - a set of gauges for the number of threads in their various states and deadlock detection.
  */
class MetricsModule {
  private[this] val log = LoggerFactory.getLogger(getClass.getName)
  lazy val registry: MetricRegistry = {
    val registry = new MetricRegistry
    registry.register("jvm.gc", new GarbageCollectorMetricSet())
    registry.register("jvm.buffers", new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer))
    registry.register("jvm.memory", new MemoryUsageGaugeSet())
    registry.register("jvm.threads", new ThreadStatesGaugeSet())
    registry
  }
}
