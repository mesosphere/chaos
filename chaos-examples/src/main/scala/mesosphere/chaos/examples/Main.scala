package mesosphere.chaos.examples

import mesosphere.chaos.examples.persons.PersonsModule
import org.rogach.scallop.ScallopConf
import mesosphere.chaos.http.{HttpConf, HttpModule}
import mesosphere.chaos.metrics.MetricsModule
import mesosphere.chaos.{ App, AppConfiguration }

object Main extends App {
  //The fact that this is lazy, allows us to pass it to a Module
  //constructor.
  lazy val conf = new ScallopConf(args) with HttpConf with AppConfiguration
  conf.afterInit()

  lazy val metricRegistry = new MetricsModule(conf).registry

  // Initializing the http service, which contains the Jetty server
  lazy val httpModule = new HttpModule(conf, metricRegistry)

  // Registering examples to the http service (add more examples here if needed)
  val personsModule = new PersonsModule(httpModule.httpService, metricRegistry)


  // Start the http service
  run(httpModule.httpService)
}