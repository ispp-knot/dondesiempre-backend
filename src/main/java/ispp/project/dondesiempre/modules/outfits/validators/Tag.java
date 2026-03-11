package ispp.project.dondesiempre.modules.outfits.validators;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Size(max = 255)
@NotBlank
@Constraint(validatedBy = {})
@Target({ FIELD, ANNOTATION_TYPE, PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Tag {

    String message() default "La tag debe medir entre 1 a 255 caracteres.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
