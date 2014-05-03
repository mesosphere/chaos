package mesosphere.chaos

import com.google.inject.{Module, Guice}
import org.rogach.scallop.ScallopConf
import com.google.common.util.concurrent.Service
import org.apache.log4j.Logger
import java.io.FileInputStream
import scala.collection.mutable.ListBuffer

/**
 * @author Florian Leibert (flo@leibert.de)
 * @author Tobi Knaup (tobi@knaup.me)
 */
trait App extends scala.App {
  import scala.collection.JavaConverters._

  lazy val injector = Guice.createInjector(modules().asJava)
  val services = ListBuffer.empty[Service]
  private val log = Logger.getLogger(getClass.getName)

  def conf(): ScallopConf with AppConfiguration

  def modules(): Iterable[_ <: Module]

  def initConf() {
    conf().afterInit()
  }

  def run(classes: Class[_ <: Service]*) {
    initConf()

    sys.addShutdownHook(shutdownAndWait())

    classes.foreach(c => {
      val service = injector.getInstance(c)
      services += service
      service.start()
    })
  }

  def shutdown() {
    log.info("Shutting down services")
    services.foreach(_.stop())
  }

  def shutdownAndWait() {
    log.info("Waiting for services to shut down")
    services.foreach(_.stopAndWait())
  }
}
