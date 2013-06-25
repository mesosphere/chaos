package mesosphere

import org.rogach.scallop.{LazyScallopConf, ScallopConf}
import com.google.inject.{Provides, AbstractModule}

case class Conf2(arguments : Array[String]) extends LazyScallopConf(arguments) {
  val port2 = opt[Int]("http_porttwo", descr = "The port to listen on for HTTP2 requests", default = Some(1212), noshort = true)
}

class FooModule(arguments : Array[String]) extends AbstractModule {

  val conf = Conf2(arguments)

  def configure() {
    println("PORT: " + conf.port2())
  }

}
