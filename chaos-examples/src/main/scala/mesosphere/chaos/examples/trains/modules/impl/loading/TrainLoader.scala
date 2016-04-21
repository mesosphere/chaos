package mesosphere.chaos.examples.trains.modules.impl.loading

import mesosphere.chaos.examples.trains.modules.impl.loading.TrainLoader.CarLoaderFactory
import mesosphere.chaos.examples.trains.modules.impl.shunting.PointSwitcher

sealed trait CarType
trait Coal extends CarType
trait Refrigerated extends CarType

class CarLoader()

class TrainLoader(craneController: CraneController,
                  pointSwitcher: PointSwitcher,
                  carLoaderFactory: CarLoaderFactory) {
  def load() = {
    println("loaded")
    pointSwitcher.switch()
  }
}

object TrainLoader {
  type CarLoaderFactory = CarType => CarLoader
}
