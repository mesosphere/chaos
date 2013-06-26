package mesosphere

import org.rogach.scallop.{Scallop, ScallopOption, ScallopConf, LazyScallopConf}

/**
 * @author Florian Leibert (flo@leibert.de)
 */
object Foobar {

  def main(arg : Array[String]) {
    val foo = new ScallopConf(arg) with C1 with C2 {

    }
    foo.afterInit

    println(foo.foo())
    println(foo.bar())
  }
}

trait C2 extends ScallopConf {
  val foo = opt[String]("foo")
}

trait C1 extends ScallopConf {
  val bar = opt[String]("bar")
}

