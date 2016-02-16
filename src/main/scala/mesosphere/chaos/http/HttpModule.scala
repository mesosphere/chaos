package mesosphere.chaos.http

import com.codahale.metrics.MetricRegistry
import mesosphere.chaos.http.impl.{ HttpServer, HttpServiceImpl }

/**
  * Provides a http service, which in it's turn contains a Jetty http server.
  * @param conf configuration needed in order to set up the Jetty server.
  * @param registry a metric registry.
  */
class HttpModule(conf: HttpConf,
                 registry: MetricRegistry) {
  lazy val httpService: HttpService = new HttpServiceImpl(new HttpServer(conf, registry))
}
