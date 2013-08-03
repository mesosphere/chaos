package mesosphere.chaos

import com.google.inject.{Module, Guice}
import org.rogach.scallop.ScallopConf
import com.google.common.util.concurrent.Service
import java.util.logging.LogManager
import java.io.FileInputStream

/**
 * @author Florian Leibert (flo@leibert.de)
 * @author Tobi Knaup (tobi@knaup.me)
 */
trait App extends scala.App {
  import scala.collection.JavaConverters._

  lazy val injector = Guice.createInjector(modules().asJava)

  def conf(): ScallopConf with AppConfiguration

  def modules(): Iterable[_ <: Module]

  def configureLogging(file: String) {
    LogManager.getLogManager.readConfiguration(new FileInputStream(file))
  }

  def initConf() {
    conf().afterInit()
    if (conf().logConfigFile.isSupplied) {
      configureLogging(conf().logConfigFile())
    }
  }

  def run(clazz: Iterable[Class[_ <: Service]]) {
    initConf()

    //TODO(FL|TK): Think about pulling this out and maybe using JSR250.
    clazz.foreach(x => injector.getInstance(x).start())
  }
}
