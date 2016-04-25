package mesosphere.chaos.examples.persons

import mesosphere.chaos.ChaosModule
import mesosphere.chaos.examples.persons.impl.PersonsResource
import mesosphere.chaos.rest.RestModule
import com.softwaremill.macwire._

class PersonsModule(chaosModule: ChaosModule) extends RestModule(chaosModule) {
  lazy val persons = Seq("Alice", "Bob", "Christoph", "Dimitri")
  lazy val personsResource = wire[PersonsResource]

  httpService.registerResources(personsResource)
}
