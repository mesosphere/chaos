package mesosphere.chaos.example

import com.codahale.metrics.MetricRegistry
import mesosphere.chaos.http.HttpService
import mesosphere.chaos.rest.RestModule

class ExampleRestModule(httpService: HttpService, metricRegistry: MetricRegistry) extends RestModule(httpService, metricRegistry) {
  httpService.registerResources(new ExampleResource)
}
