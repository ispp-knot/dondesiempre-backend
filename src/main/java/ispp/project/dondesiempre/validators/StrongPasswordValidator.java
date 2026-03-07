package ispp.project.dondesiempre.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

  private static final Pattern UPPERCASE =
      Pattern.compile(".*\\p{Lu}.*"); // Unicode: Uppercase Letter
  private static final Pattern DIGIT = Pattern.compile(".*\\p{N}.*"); // Unicode: Number
  private static final Pattern SYMBOL =
      Pattern.compile(".*[\\p{S}\\p{P}].*"); // Unicode: Symbol or Punctuation

  @Override
  public void initialize(StrongPassword constraintAnnotation) {
    // no-op
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }
    return value.length() >= 8
        && UPPERCASE.matcher(value).matches()
        && DIGIT.matcher(value).matches()
        && SYMBOL.matcher(value).matches();
  }
}
