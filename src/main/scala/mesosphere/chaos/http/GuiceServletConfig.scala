package mesosphere.chaos.http

import com.google.inject.{ Injector, Inject }
import com.google.inject.servlet.GuiceServletContextListener
import javax.servlet.ServletContextEvent

//TODO(FL|TK): Allow passing in doWithContext closure to remove coupling to Registry.
class GuiceServletConfig @Inject() (val injector: Injector) extends GuiceServletContextListener {

  override def contextInitialized(servletContextEvent: ServletContextEvent) {
    super.contextInitialized(servletContextEvent)
  }

  def getInjector = injector
}
