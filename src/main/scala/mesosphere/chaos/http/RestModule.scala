package mesosphere.chaos.http

import com.sun.jersey.guice.spi.container.servlet.GuiceContainer
import com.google.inject.servlet.ServletModule
/**
  * Base class for REST modules.
  */

class RestModule extends ServletModule {

  // Override these in a subclass to mount resources at a different path
  val guiceContainerUrl = "/*"

  protected override def configureServlets() {
    serve(guiceContainerUrl).`with`(classOf[GuiceContainer])
  }
}

