package mesosphere.validation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import javax.validation.ConstraintViolation;
import java.util.Set;

public class ValidationErrorMessage {
    private final ImmutableList<String> errors;

    public ValidationErrorMessage(Set<ConstraintViolation<?>> errors) {
        this.errors = ImmutableList.copyOf(Collections2.transform(errors, new Function<ConstraintViolation<?>, String>() {
          @Nullable
          @Override
          public String apply(@Nullable ConstraintViolation<?> constraintViolation) {
            return constraintViolation.getMessage();
          }
        }));
    }

    @JsonProperty
    public ImmutableList<String> getErrors() {
        return errors;
    }
}