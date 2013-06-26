package mesosphere

import org.rogach.scallop.ScallopConf

object SampleApp extends Application {

  def getModules() = {
    Seq(new HttpModule(getConfiguration))
  }

  lazy val getConfiguration = new ScallopConf(args) with HttpConf

  run(List(classOf[HttpService]))

}
