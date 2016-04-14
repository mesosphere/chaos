package mesosphere.chaos.examples.trains.modules

import com.softwaremill.macwire._
import mesosphere.chaos.examples.trains.modules.impl.shunting._

@Module
trait ShuntingModule extends InterceptorLogging {
  lazy val pointSwitcher = logEvents(wire[PointSwitcher])

  // dependency of the module
  def trainShunter: TrainShunter
}

trait TraditionalShuntingModule extends ShuntingModule {
  lazy val trainCarCoupler = logEvents(wire[TrainCarCoupler])
  lazy val trainShunter = logEvents(wire[TraditionalTrainShunter])
}

trait ModernShuntingModule extends ShuntingModule {
  lazy val trainShunter = logEvents(wire[TeleportingTrainShunter])
}
