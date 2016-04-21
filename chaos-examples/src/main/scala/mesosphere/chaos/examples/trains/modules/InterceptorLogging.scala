package mesosphere.chaos.examples.trains.modules

import com.softwaremill.macwire.aop.Interceptor

trait InterceptorLogging {
  /**
    * Using interceptors is a two-step process.
    * First, we have to declare what should be intercepted.
    * Ideally, this shouldn’t involve the implementation of the interceptor in any way.
    * Secondly, we have to define what the interceptor does - the behaviour.
    */
  def logEvents: Interceptor
}
