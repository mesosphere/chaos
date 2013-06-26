package mesosphere.chaos.example

import com.google.inject.servlet.ServletModule
import com.google.inject.{Scopes, Provides, Singleton}
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import com.fasterxml.jackson.databind.ObjectMapper
import com.codahale.metrics.servlets.{MetricsServlet, PingServlet}

/**
 * @author Tobi Knaup
 */

class ExampleRestModule extends ServletModule {

  protected override def configureServlets() {
    bind(classOf[PingServlet]).in(Scopes.SINGLETON)
    bind(classOf[MetricsServlet]).in(Scopes.SINGLETON)
    bind(classOf[ExampleResource]).in(Scopes.SINGLETON)

    serve("/ping").`with`(classOf[PingServlet])
    serve("/metrics").`with`(classOf[MetricsServlet])
    serve("/*").`with`(classOf[GuiceContainer])
  }

  @Provides
  @Singleton
  def provideJacksonJsonProvider = {
    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    new JacksonJsonProvider(mapper)
  }
}
