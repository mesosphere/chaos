package mesosphere.chaos.examples.persons.impl

import javax.ws.rs.core.MediaType
import javax.ws.rs.{ GET, Produces }

import scala.Array

@Produces(Array(MediaType.APPLICATION_JSON))
class PersonsSubResource {

  @GET
  def get() = System.getProperties
}
