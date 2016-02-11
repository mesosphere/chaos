package mesosphere.chaos.examples.persons

import com.codahale.metrics.MetricRegistry
import mesosphere.chaos.examples.persons.impl.PersonsResource
import mesosphere.chaos.http.HttpService
import mesosphere.chaos.rest.RestModule

class PersonsModule(httpService: HttpService, metricRegistry: MetricRegistry) extends RestModule(httpService, metricRegistry) {
  httpService.registerResources(new PersonsResource(Seq("Alice", "Bob", "Christoph", "Dimitri")))
}
