package mesosphere.chaos.examples.persons.impl

case class Person(name: String, age: Int) {
  override def toString = {
    s"Person($name, $age)"
  }
}

object Person {
  val maxAge = 90
}
