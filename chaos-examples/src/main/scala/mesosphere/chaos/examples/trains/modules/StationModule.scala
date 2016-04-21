package mesosphere.chaos.examples.trains.modules

import akka.actor.{ActorSystem, ActorRef, Props}
import com.softwaremill.macwire._
import com.softwaremill.tagging._
import mesosphere.chaos.examples.trains.modules.impl.loading.TrainLoader
import mesosphere.chaos.examples.trains.modules.impl.station._

@Module
trait StationModule extends ShuntingModule with LoadingModule {
  // Factory function
  def trainStation(name: String) = logEvents(wire[TrainStation])

  // Multiple instances via tagging
  lazy val regularTrainLoader = wire[TrainLoader].taggedWith[Regular]
  lazy val liquidTrainLoader = wire[TrainLoader].taggedWith[Liquid]

  lazy val trainDispatch = logEvents(wire[TrainDispatch])

  // Actor wiring

  // actor system module dependency
  def actorSystem: ActorSystem

  def createReactiveTrainDispatch(loadListener: ActorRef @@ LoadListener) =
    actorSystem.actorOf(Props(wire[ReactiveTrainDispatch]))
}
