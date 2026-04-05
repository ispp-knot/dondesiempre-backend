package ispp.project.dondesiempre.modules.stores.validators;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ContactLinkValidator.class)
@Documented
public @interface ValidContactLink {
    
    String message() default "Must be a valid link or telephone number.";
    
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {}; 
}