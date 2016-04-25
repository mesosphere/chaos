package mesosphere.chaos.metrics.impl

import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.jvm.ThreadStatesGaugeSet
import com.codahale.metrics.jvm.MemoryUsageGaugeSet
import com.codahale.metrics.jvm.BufferPoolMetricSet
import com.codahale.metrics.jvm.GarbageCollectorMetricSet

class DefaultMetricRegistry(garbageCollectorMetricSet: GarbageCollectorMetricSet,
                            bufferPoolMetricSet: BufferPoolMetricSet,
                            memoryUsageGaugeSet: MemoryUsageGaugeSet,
                            threadStatesGaugeSet: ThreadStatesGaugeSet) extends MetricRegistry {
  register("jvm.gc", garbageCollectorMetricSet)
  register("jvm.buffers", bufferPoolMetricSet)
  register("jvm.memory", memoryUsageGaugeSet)
  register("jvm.threads", threadStatesGaugeSet)
}
