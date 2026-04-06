package ispp.project.dondesiempre.modules.stores.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class ContactLinkValidator implements ConstraintValidator<ValidContactLink, String> {

  private static final String URL_REGEX =
      "^(https?://)(([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}|localhost|(\\d{1,3}\\.){3}\\d{1,3})(:\\d+)?(/[\\w\\d.\\-!$%&'()*+,;=:@?#|%]*)*$";
  private static final String TEL_URI_REGEX = "^tel:\\+?[1-9]\\d{1,14}$|^tel:[0-9]{9,15}$";

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.trim().isEmpty()) {
      return true;
    }

    boolean isUrl = Pattern.compile(URL_REGEX, Pattern.CASE_INSENSITIVE).matcher(value).matches();
    boolean isTel = Pattern.compile(TEL_URI_REGEX).matcher(value).matches();

    return isUrl || isTel;
  }
}
