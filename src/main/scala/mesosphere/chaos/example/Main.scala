package mesosphere.chaos.example

import org.rogach.scallop.ScallopConf
import mesosphere.chaos.http.{HttpService, HttpConf, HttpModule}
import mesosphere.Application

object Main extends Application {

  def getModules() = {
    Seq(
      new HttpModule(getConfiguration),
      new ExampleRestModule
    )
  }

  lazy val getConfiguration = new ScallopConf(args) with HttpConf

  run(List(classOf[HttpService]))

}
