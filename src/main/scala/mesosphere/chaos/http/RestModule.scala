package mesosphere.chaos.http

import com.codahale.metrics.servlets.{MetricsServlet, PingServlet}
import com.google.inject.{Singleton, Provides, Scopes}
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import javax.validation.Validation
import com.google.inject.servlet.ServletModule
import mesosphere.chaos.validation.{JacksonMessageBodyProvider, ConstraintViolationExceptionMapper}

/**
 * Base class for REST modules.
 *
 * @author Tobi Knaup
 */

class RestModule extends ServletModule {

  protected override def configureServlets() {
    bind(classOf[PingServlet]).in(Scopes.SINGLETON)
    bind(classOf[MetricsServlet]).in(Scopes.SINGLETON)
    bind(classOf[LogConfigServlet]).in(Scopes.SINGLETON)
    bind(classOf[ConstraintViolationExceptionMapper]).in(Scopes.SINGLETON)

    serve("/ping").`with`(classOf[PingServlet])
    serve("/metrics").`with`(classOf[MetricsServlet])
    serve("/logging").`with`(classOf[LogConfigServlet])
    serve("/*").`with`(classOf[GuiceContainer])
  }

  @Provides
  @Singleton
  def provideJacksonJsonProvider: JacksonJsonProvider = {
    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    new JacksonMessageBodyProvider(mapper, Validation.buildDefaultValidatorFactory().getValidator)
  }
}