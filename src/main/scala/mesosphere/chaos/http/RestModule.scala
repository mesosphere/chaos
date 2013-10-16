package mesosphere.chaos.http

import com.codahale.metrics.servlets.MetricsServlet
import com.google.inject.{Singleton, Provides, Scopes}
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import javax.validation.Validation
import com.google.inject.servlet.ServletModule
import mesosphere.chaos.validation.{JacksonMessageBodyProvider, ConstraintViolationExceptionMapper}
import javax.inject.Named
import com.google.inject.name.Names

/**
 * Base class for REST modules.
 *
 * @author Tobi Knaup
 */

class RestModule extends ServletModule {

  // Override these in a subclass to mount resources at a different path
  val pingUrl = "/ping"
  val metricsUrl = "/metrics"
  val loggingUrl = "/logging"
  val guiceContainerUrl = "/*"

  protected override def configureServlets() {
    bind(classOf[ObjectMapper])
      .annotatedWith(Names.named("restMapper"))
      .toInstance(new ObjectMapper())

    bind(classOf[PingServlet]).in(Scopes.SINGLETON)
    bind(classOf[MetricsServlet]).in(Scopes.SINGLETON)
    bind(classOf[LogConfigServlet]).in(Scopes.SINGLETON)
    bind(classOf[ConstraintViolationExceptionMapper]).in(Scopes.SINGLETON)

    serve(pingUrl).`with`(classOf[PingServlet])
    serve(metricsUrl).`with`(classOf[MetricsServlet])
    serve(loggingUrl).`with`(classOf[LogConfigServlet])
    serve(guiceContainerUrl).`with`(classOf[GuiceContainer])
  }

  @Provides
  @Singleton
  def provideJacksonJsonProvider(@Named("restMapper") mapper: ObjectMapper): JacksonJsonProvider = {
    mapper.registerModule(DefaultScalaModule)
    new JacksonMessageBodyProvider(mapper, Validation.buildDefaultValidatorFactory().getValidator)
  }
}
