package mesosphere.chaos.example

import mesosphere.chaos.ChaosModule
import mesosphere.chaos.rest.RestModule

class ExampleRestModule(chaosModule: ChaosModule) extends RestModule(chaosModule) {
  httpService.registerResources(new ExampleResource)
}
