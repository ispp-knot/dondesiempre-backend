package ispp.project.dondesiempre.validators;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = HexColorValidator.class)
@Target({FIELD, METHOD, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HexColor {
  String message() default "must be a valid hex color (e.g. #RRGGBB or #RGB)";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
