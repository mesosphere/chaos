package mesosphere.chaos.metrics.impl

import java.lang.management.ManagementFactory
import com.codahale.metrics.jvm.BufferPoolMetricSet

class DefaultBufferPoolMetricSet extends BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer)
