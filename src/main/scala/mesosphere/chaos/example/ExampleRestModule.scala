package mesosphere.chaos.example

import com.google.inject.servlet.ServletModule
import com.google.inject.{Provides, Singleton}
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import com.fasterxml.jackson.databind.ObjectMapper

/**
 * @author Tobi Knaup
 */

class ExampleRestModule extends ServletModule {

  protected override def configureServlets() {
    serve("/*").`with`(classOf[GuiceContainer])
    bind(classOf[ExampleResource]).in(classOf[Singleton])
  }

  @Provides
  @Singleton
  def provideJacksonJsonProvider = {
    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    new JacksonJsonProvider(mapper)
  }

}
