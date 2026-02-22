package ispp.project.dondesiempre.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PhoneValidator implements ConstraintValidator<Phone, String> {

  private static final Pattern PATTERN = Pattern.compile("^(\\+\\d{1,3}[- ]?)?\\d{7,15}$");

  @Override
  public void initialize(Phone constraintAnnotation) {
    // no-op
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }
    return PATTERN.matcher(value).matches();
  }
}
