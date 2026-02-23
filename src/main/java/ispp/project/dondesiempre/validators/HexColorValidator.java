package ispp.project.dondesiempre.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class HexColorValidator implements ConstraintValidator<HexColor, String> {

  private static final Pattern PATTERN = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");

  @Override
  public void initialize(HexColor constraintAnnotation) {
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
