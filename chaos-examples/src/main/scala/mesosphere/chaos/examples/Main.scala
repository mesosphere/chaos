package mesosphere.chaos.examples

import mesosphere.chaos.http.HttpConf
import mesosphere.chaos.{ AppConfiguration, ChaosModule, App }
import mesosphere.chaos.examples.persons.PersonsModule
import org.rogach.scallop.ScallopConf

object Main extends App {
  lazy val conf = new ScallopConf(args) with HttpConf with AppConfiguration {
    verify()
  }

  // Creating chaos module, which contains ScallopConf, MetricsModule and HttpModule.
  val chaosModule = new ChaosModule(conf)
  // Derives from RestModule adding some example resources to it.
  val exampleModule = new PersonsModule(chaosModule)

  run(chaosModule.httpModule.httpService)
}