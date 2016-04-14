package mesosphere.chaos.examples.trains.modules

import com.softwaremill.macwire._
import mesosphere.chaos.examples.trains.modules.impl.loading.{CarLoader, CarType, CraneController, TrainLoader}
import mesosphere.chaos.examples.trains.modules.impl.shunting.PointSwitcher

@Module
trait LoadingModule extends InterceptorLogging {
  lazy val craneController = logEvents(wire[CraneController])
  lazy val trainLoader = logEvents(wire[TrainLoader])

  // Factories as functions
  lazy val carLoaderFactory = (ct: CarType) => wire[CarLoader]

  // dependency of the module
  def pointSwitcher: PointSwitcher
}
