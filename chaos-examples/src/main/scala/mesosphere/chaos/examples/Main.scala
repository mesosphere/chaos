package mesosphere.chaos.examples

import mesosphere.chaos.http.HttpConf
import mesosphere.chaos.{ AppConfiguration, App }
import mesosphere.chaos.examples.persons.PersonsModule
import org.rogach.scallop.ScallopConf

object Main extends App {
  lazy val conf = new ScallopConf(args) with HttpConf with AppConfiguration
  conf.afterInit()

  // Creating persons module, which contains ScallopConf, MetricsModule and HttpModule.
  val personsModule = new PersonsModule { def httpConf = conf }

  run(personsModule.httpService)
}
