package ispp.project.dondesiempre.modules.stores.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class ContactLinkValidator implements ConstraintValidator<ValidContactLink, String> {

  private static final String URL_REGEX =
      "^(https?://)?(([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}|localhost|(\\d{1,3}\\.){3}\\d{1,3})(:\\d+)?(/[^\\s]*)?$";

  private static final String PHONE_REGEX = "^\\+?[1-9]\\d{8,14}$";

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.trim().isEmpty()) {
      return true;
    }

    String cleanPhone = value.replaceAll("\\s+", "");

    boolean isUrl = Pattern.compile(URL_REGEX, Pattern.CASE_INSENSITIVE).matcher(value).matches();
    boolean isPhone = Pattern.compile(PHONE_REGEX).matcher(cleanPhone).matches();

    return isUrl || isPhone;
  }
}
