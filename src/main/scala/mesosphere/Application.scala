package mesosphere

import com.google.inject.{Module, Guice}
import com.google.common.util.concurrent.AbstractIdleService
import org.rogach.scallop.ScallopConf

/**
 * @author Florian Leibert (flo@leibert.de)
 * @author Tobi Knaup (tobi@knaup.me)
 */
trait Application extends App {

  def getConfiguration() : ScallopConf

  def getModules(): Iterable[_ <: Module]

  def run[T <: AbstractIdleService](clazz : List[Class[T]]) {
    getConfiguration().afterInit()

    import scala.collection.JavaConverters._
    val injector = Guice.createInjector(getModules().asJava)

    //TODO(FL|TK): Think about pulling this out and maybe using JSR250.
    clazz.foreach(x => injector.getInstance(x).start())

  }


}
