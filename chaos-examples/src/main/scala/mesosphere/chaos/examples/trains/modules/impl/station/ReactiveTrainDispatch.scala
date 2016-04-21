package mesosphere.chaos.examples.trains.modules.impl.station

import com.softwaremill.tagging.@@
import akka.actor.{ActorRef, Actor}
import mesosphere.chaos.examples.trains.modules.impl.loading.{Full, TrainLoader}

case object Load
trait LoadListener

class ReactiveTrainDispatch(
                             trainLoader: TrainLoader @@ Regular,
                             trainDispatch: TrainDispatch,
                             loadListener: ActorRef @@ LoadListener) extends Actor {
  def receive = {
    case Load => loadListener ! Load
    case Full => println("reactively fully loaded")
    case _ => println("unknown message type")
  }
}
