package mesosphere.chaos.examples.trains.modules.impl.loading

import akka.actor.Actor
import mesosphere.chaos.examples.trains.modules.impl.station.Load

case object Full

class LoadListenerActor extends Actor {
  override def receive: Receive = {
    case Load => sender() ! Full
    case _ => println("unknown message type")
  }
}
