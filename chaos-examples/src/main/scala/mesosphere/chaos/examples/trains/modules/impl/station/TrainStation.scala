package mesosphere.chaos.examples.trains.modules.impl.station

import com.softwaremill.tagging.@@
import mesosphere.chaos.examples.trains.modules.impl.loading.TrainLoader
import mesosphere.chaos.examples.trains.modules.impl.shunting.TrainShunter

trait Regular
trait Liquid

class TrainStation(name: String,
                   trainShunter: TrainShunter,
                   regularTrainLoader: TrainLoader @@ Regular,
                   liquidTrainLoader: TrainLoader @@ Liquid,
                   trainDispatch: TrainDispatch) {

  def prepareAndDispatchNextTrain() = {
    trainShunter.shunt()
    regularTrainLoader.load()
    liquidTrainLoader.load()
    trainDispatch.dispatch()
  }
}
