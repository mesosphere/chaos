package mesosphere.chaos.http

import javax.servlet.{Filter, Servlet}

import com.google.common.util.concurrent.AbstractIdleService

trait HttpService extends AbstractIdleService {
  def registerResources(resources: AnyRef*)
  def getRegisteredResources: Seq[AnyRef]

  def registerServlet(servlet: Servlet, path: String)
  def registerFilter(filter: Filter, path: String)

  def getRegisteredServlets: Seq[Servlet]
  def getRegisteredFilter: Seq[Filter]
}
