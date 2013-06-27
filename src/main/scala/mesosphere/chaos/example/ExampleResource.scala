package mesosphere.chaos.example

import javax.ws.rs.{POST, Produces, Path, GET}
import javax.ws.rs.core.MediaType
import com.codahale.metrics.annotation.Timed
import org.hibernate.validator.constraints.{Range, NotEmpty}
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

/**
 * @author Tobi Knaup
 */

class SampleObject {
  @JsonProperty @Range(min = 0, max = 150, message = "ERROR") var age : Int = 0
  @JsonProperty @NotEmpty(message = "ERROR") var name : String = ""
}

@Path("foo")
@Produces(Array(MediaType.APPLICATION_JSON))
class ExampleResource {

  @Timed(name = "foo")
  @GET
  def foo() = {
    new SampleObject()
  }

  @POST
  def bar(@Valid obj : SampleObject) {
    println("YAY")
    println(obj.age)
    println(obj.name)
  }

}
