package mesosphere.chaos.example

import javax.ws.rs.{Produces, Path, GET}
import javax.ws.rs.core.MediaType

/**
 * @author Tobi Knaup
 */

@Path("foo")
@Produces(Array(MediaType.APPLICATION_JSON))
class ExampleResource {

  @GET
  def foo() = Map("foo" -> "bar")

}
