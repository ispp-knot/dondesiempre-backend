package ispp.project.dondesiempre.modules.promotions.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = StartDateBeforeEndDateValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface StartDateBeforeEndDate {
  String message() default "La fecha de fin debe ser posterior a la fecha de inicio";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
