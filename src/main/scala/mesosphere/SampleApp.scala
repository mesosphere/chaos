package mesosphere

import com.google.common.util.concurrent.AbstractIdleService
import com.google.inject.Guice
import org.rogach.scallop.{Scallop, LazyScallopConf}

class SampleApp(port : Int) extends AbstractIdleService {

  def startWebServer() {
  }

  def startUp() { println("Starting up.") }

  def shutDown() { println("Shutting down.") }

}



object SampleApp extends Application {

  def getModules() = {
    Seq(new HttpModule(args), new FooModule(args))
  }
  run()
}
