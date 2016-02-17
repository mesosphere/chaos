package mesosphere.chaos.examples.persons

import mesosphere.chaos.ChaosModule
import mesosphere.chaos.examples.persons.impl.PersonsResource
import mesosphere.chaos.rest.RestModule

class PersonsModule(chaosModule: ChaosModule) extends RestModule(chaosModule) {
  httpService.registerResources(new PersonsResource(Seq("Alice", "Bob", "Christoph", "Dimitri")))
}
