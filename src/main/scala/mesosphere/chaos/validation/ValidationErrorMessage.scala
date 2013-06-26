package mesosphere.chaos.validation

import javax.validation.ConstraintViolation
import scala.collection.JavaConverters._

/**
 * @author Tobi Knaup
 */

class ValidationErrorMessage(violations: java.util.Set[ConstraintViolation[_]]) {

  val errors = violations.asScala.map(violation => new AttributeErrorMessage(violation))

  class AttributeErrorMessage(violation: ConstraintViolation[_]) {
    val attribute = violation.getPropertyPath.toString
    val error = violation.getMessage
  }

}
