package mesosphere.chaos.example

import org.rogach.scallop.ScallopConf
import mesosphere.chaos.http.{HttpConf, HttpModule}
import mesosphere.chaos.metrics.MetricsModule
import mesosphere.chaos.{ App, AppConfiguration }

// TODO: put the whole example somewhere else
object Main extends App {
  //The fact that this is lazy, allows us to pass it to a Module
  //constructor.
  lazy val conf = new ScallopConf(args) with HttpConf with AppConfiguration
  conf.afterInit()

  lazy val metricRegistry = new MetricsModule(conf).registry
  lazy val httpModule = new HttpModule(conf, metricRegistry)
  val exampleModule = new ExampleRestModule(httpModule.httpService, metricRegistry)

  run(httpModule.httpService)
}

