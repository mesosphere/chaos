package mesosphere

import com.google.inject.{Module, Guice}
import org.rogach.scallop.ScallopConf
import com.google.common.util.concurrent.Service
import java.util.logging.LogManager
import java.io.FileInputStream
import mesosphere.chaos.AppConfiguration

/**
 * @author Florian Leibert (flo@leibert.de)
 * @author Tobi Knaup (tobi@knaup.me)
 */
trait Application extends App {

  def getConfiguration(): ScallopConf with AppConfiguration

  def getModules(): Iterable[_ <: Module]

  def configureLogging(file: String) {
    LogManager.getLogManager().readConfiguration(new FileInputStream(file))
  }

  def run(clazz: List[Class[_ <:Service]]) {
    getConfiguration().afterInit()
    if (getConfiguration().logConfigFile.isSupplied) {
      configureLogging(getConfiguration.logConfigFile())
    }

    import scala.collection.JavaConverters._
    val injector = Guice.createInjector(getModules().asJava)

    //TODO(FL|TK): Think about pulling this out and maybe using JSR250.
    clazz.foreach(x => injector.getInstance(x).start())
  }


}
