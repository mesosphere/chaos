package mesosphere.chaos.examples.trains.modules.impl.shunting

trait TrainShunter { def shunt() = println("shunted") }
class TraditionalTrainShunter(pointSwitcher: PointSwitcher,
                              trainCarCoupler: TrainCarCoupler) extends TrainShunter {
  override def shunt() = {
    pointSwitcher.switch()
    trainCarCoupler.couple()
    super.shunt()
  }
}
class TeleportingTrainShunter() extends TrainShunter {
  override def shunt() = {
    println("teleported")
  }
}
