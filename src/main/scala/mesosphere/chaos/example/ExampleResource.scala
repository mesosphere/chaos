package mesosphere.chaos.example

import javax.ws.rs.{Produces, Path, GET}
import javax.ws.rs.core.MediaType
import com.codahale.metrics.annotation.Timed

/**
 * @author Tobi Knaup
 */

@Path("foo")
@Produces(Array(MediaType.APPLICATION_JSON))
class ExampleResource {

  @Timed(name = "foo")
  @GET
  def foo() = {
    Map("foo" -> "bar")
  }

}
