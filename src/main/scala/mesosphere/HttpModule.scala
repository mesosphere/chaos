package mesosphere

import com.google.inject.{Provides, AbstractModule}
import org.eclipse.jetty.server.Server
import org.rogach.scallop._

/**
 * @author Florian Leibert (flo@leibert.de)
 */
case class Conf(arguments : Array[String]) extends ScallopConf(arguments) {
  val port = opt[Int]("http_port", descr = "The port to listen on for HTTP requests", default = Some(8080), noshort = true)
}

class HttpModule(arguments : Array[String]) extends AbstractModule {
  val conf = Conf(arguments)
  def configure() {
    bind(classOf[HttpService])
  }

  @Provides
  def provideHttpServer() = {
    new Server(conf.port())
  }
}
