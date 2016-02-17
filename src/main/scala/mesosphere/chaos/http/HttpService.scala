package mesosphere.chaos.http

import javax.servlet.{ Filter, Servlet }

import com.google.common.util.concurrent.AbstractIdleService

/**
  * This Guava interface supposed to provide methods for registering resources, servlets or filters to a http server.
  * One should add all desired resources prior executing the service. Then an instance of this trait might be started
  * by passing it to a Guava ServiceManager instance and applying startAsync() function. This functionality is
  * implemented in [[mesosphere.chaos.App.run(services: Service*)]]. You could derive from that class and pass a
  * [[mesosphere.chaos.http.HttpService]] to it's run function.
  */
trait HttpService extends AbstractIdleService {
  /**
    * You may register one or several resources e.g. instances of Jersey JAX-RS annotated REST classes.
    * @param resources instances you would like to add to your server.
    *                  That might be e.g. an instance of a JAX-RS annotated Jersey class.
    */
  def registerResources(resources: AnyRef*)

  /**
    * Returns a lilst of resources that been registered on the server.
    * @return
    */
  def getRegisteredResources: Seq[AnyRef]

  /**
    * Registers a servlet.
    * @param servlet servlet you would like to register.
    * @param path path it should be binded to.
    */
  def registerServlet(servlet: Servlet, path: String)

  /**
    * Returns a list of registered servlets.
    * @return registered servlets.
    */
  def getRegisteredServlets: Seq[Servlet]

  /**
    * Registers a filter.
    * @param filter filter you would like to register on your server.
    * @param path path it should be binded to.
    */
  def registerFilter(filter: Filter, path: String)

  /**
    * Returns registered filters.
    * @return registered filters.
    */
  def getRegisteredFilter: Seq[Filter]
}
