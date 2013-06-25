package mesosphere

import com.google.inject.{Module, Guice, AbstractModule}
import com.google.common.util.concurrent.AbstractService

/**
 * @author Florian Leibert (flo@leibert.de)
 */
trait Application extends App {

  def getModules() : Iterable[_ <: Module]

  def run() {
    import scala.collection.JavaConverters._
    val injector = Guice.createInjector(getModules().asJava)
    //TODO(FL|TK): Think about pulling this out and maybe using JSR250.
    val httpService = injector.getInstance(classOf[HttpService])

    httpService.startAndWait()
  }
}
