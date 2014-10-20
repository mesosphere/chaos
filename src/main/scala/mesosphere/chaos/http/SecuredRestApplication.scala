package mesosphere.chaos.http

import scala.collection.JavaConversions._
import javax.ws.rs.core.Application
import org.secnod.shiro.jaxrs.ShiroExceptionMapper
import org.secnod.shiro.jersey.SubjectInjectableProvider

class SecuredRestApplication extends Application {

  override def getSingletons(): java.util.Set[Object] = {
    Set(
      new SubjectInjectableProvider(),
      new ShiroExceptionMapper())
  }
}
