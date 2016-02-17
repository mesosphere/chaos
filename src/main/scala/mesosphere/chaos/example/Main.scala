package mesosphere.chaos.example

import mesosphere.chaos.http.HttpConf
import mesosphere.chaos.{AppConfiguration, ChaosModule, App}
import org.rogach.scallop.ScallopConf

// TODO: put the whole example somewhere else
object Main extends App {
  //The fact that this is lazy, allows us to pass it to a Module
  //constructor.
  lazy val conf = new ScallopConf(args) with HttpConf with AppConfiguration
  conf.afterInit()

  // Creating chaos module, which contains ScallopConf, MetricsModule and HttpModule.
  val chaosModule = new ChaosModule(conf)
  // Derives from RestModule adding some example resources to it.
  val exampleModule = new ExampleRestModule(chaosModule)

  run(chaosModule.httpModule.httpService)
}

