package mesosphere.chaos.examples.persons.impl

import javax.validation.Valid
import javax.ws.rs.core.MediaType
import javax.ws.rs.{ GET, POST, Path, Produces }

import com.codahale.metrics.annotation.Timed

import scala.util.Random

@Path("persons")
@Produces(Array(MediaType.APPLICATION_JSON))
class PersonsResource(names: Seq[String]) {
  @GET
  @Timed
  def get() = Person(names(Random.nextInt(names.size)), Random.nextInt(Person.maxAge))

  @POST
  @Timed
  def post(@Valid person: Person) = println(person)

  @Path("system")
  def bar() = new PersonsSubResource
}
