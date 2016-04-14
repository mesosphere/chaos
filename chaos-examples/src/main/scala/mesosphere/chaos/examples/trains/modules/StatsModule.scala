package mesosphere.chaos.examples.trains.modules

import com.softwaremill.macwire._
import mesosphere.chaos.examples.trains.modules.impl.stats.{LoadingStats, ShuntingStats}

class StatsModule(shuntingModule: ShuntingModule, loadingModule: LoadingModule) {
  lazy val loadingStats = wire[LoadingStats]
  lazy val shuntingStats = wire[ShuntingStats]
}
