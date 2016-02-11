package mesosphere.chaos.http.impl

import com.fasterxml.jackson.databind.{ObjectMapper, Module}
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import scala.collection.JavaConverters._

/**
  * Used to map objects to JSON scheme. Is added as resource to the servlet container.
  */
class JsonProvider extends JacksonJaxbJsonProvider {
  val jacksonModules: Iterable[Module] = Seq(DefaultScalaModule)
  val mapper = new ObjectMapper()
  mapper.registerModules(jacksonModules.asJava)
  this.setMapper(mapper)
}
