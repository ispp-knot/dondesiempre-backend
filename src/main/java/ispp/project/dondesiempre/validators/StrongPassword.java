package ispp.project.dondesiempre.validators;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({FIELD, METHOD, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {
  String message() default
      "must be at least 8 characters and contain at least one uppercase letter, one number, and one"
          + " symbol";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
