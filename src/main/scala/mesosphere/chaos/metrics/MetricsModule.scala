package mesosphere.chaos.metrics

import com.codahale.metrics.jvm._
import mesosphere.chaos.metrics.impl.{ DefaultBufferPoolMetricSet, DefaultMetricRegistry }
import org.slf4j.LoggerFactory

import com.softwaremill.macwire._

/**
  * Includes a registry of metric instances. Those contain:
  *  - a set of gauges for the counts and elapsed times of garbage collections.
  *  - a set of gauges for the count, usage, and capacity of the JVM's direct and mapped buffer pools.
  *  - a set of gauges for JVM memory usage, including stats on heap vs. non-heap memory, plus
  *    GC-specific memory pools.
  *  - a set of gauges for the number of threads in their various states and deadlock detection.
  */
@Module
class MetricsModule {
  private[this] val log = LoggerFactory.getLogger(getClass.getName)

  lazy val garbageCollector = wire[GarbageCollectorMetricSet]
  lazy val threadState = wire[ThreadStatesGaugeSet]
  lazy val memoryUsage = wire[MemoryUsageGaugeSet]
  lazy val bufferPool = wire[DefaultBufferPoolMetricSet]
  lazy val registry = wire[DefaultMetricRegistry]
}
