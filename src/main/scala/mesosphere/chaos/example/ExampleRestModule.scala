package mesosphere.chaos.example

import com.google.inject.servlet.ServletModule
import com.google.inject.Singleton
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer
import org.codehaus.jackson.jaxrs.JacksonJsonProvider

/**
 * @author Tobi Knaup
 */

class ExampleRestModule extends ServletModule {

  protected override def configureServlets() {
    serve("/*").`with`(classOf[GuiceContainer])
    bind(classOf[JacksonJsonProvider]).in(classOf[Singleton])
    bind(classOf[ExampleResource]).in(classOf[Singleton])
  }

}
