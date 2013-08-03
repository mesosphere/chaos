package mesosphere.chaos.example

import org.rogach.scallop.ScallopConf
import mesosphere.chaos.http.{HttpService, HttpConf, HttpModule}
import mesosphere.chaos.metrics.MetricsModule
import mesosphere.chaos.{App, AppConfiguration}

object Main extends App {

  def modules() = {
    Seq(
      new HttpModule(conf),
      new MetricsModule,
      new ExampleRestModule
    )
  }

  lazy val conf = new ScallopConf(args)
    with HttpConf with AppConfiguration

  run(List(classOf[HttpService]))
}
