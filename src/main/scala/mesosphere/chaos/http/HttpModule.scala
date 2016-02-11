package mesosphere.chaos.http

import com.codahale.metrics.MetricRegistry
import mesosphere.chaos.http.impl.{HttpServer, HttpServiceImpl}

class HttpModule(conf: HttpConf,
                 registry: MetricRegistry) {
  lazy val httpService: HttpService = new HttpServiceImpl(new HttpServer(conf, registry))
}
