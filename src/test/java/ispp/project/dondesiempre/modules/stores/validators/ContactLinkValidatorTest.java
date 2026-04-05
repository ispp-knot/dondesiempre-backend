package ispp.project.dondesiempre.modules.stores.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class ContactLinkValidatorTest {

  private final ContactLinkValidator validator = new ContactLinkValidator();

  @Test
  void shouldPass_WhenNull() {
    assertTrue(validator.isValid(null, null));
  }

  @Test
  void shouldPass_WhenEmpty() {
    assertTrue(validator.isValid("", null));
    assertTrue(validator.isValid("   ", null));
  }

  @Test
  void shouldAccept_WhenValidUrl() {
    assertTrue(validator.isValid("https://github.com/ispp-knot", null));
    assertTrue(validator.isValid("http://localhost:3000/test", null));
    assertTrue(validator.isValid("www.google.com", null));
    assertTrue(validator.isValid("instagram.com/user", null));
  }

  @Test
  void shouldAccept_WhenComplexUrlWithSpecialCharacters() {
    assertTrue(validator.isValid("https://github.com/orgs/ispp-knot/projects/1/views/1?pane=issue&itemId=168", null));
  }

  @Test
  void shouldAccept_WhenValidInternationalPhone() {
    assertTrue(validator.isValid("+34600123456", null));
    assertTrue(validator.isValid("+15551234567", null));
  }

  @Test
  void shouldAccept_WhenPhoneWithSpaces() {
    assertTrue(validator.isValid("+34 600 123 456", null));
  }

  @Test
  void shouldAccept_WhenNationalPhone() {
    assertTrue(validator.isValid("954123456", null));
  }

  @Test
  void shouldReject_WhenRandomText() {
    assertFalse(validator.isValid("esto-no-es-nada", null));
  }

  @Test
  void shouldReject_WhenTooShortPhone() {
    assertFalse(validator.isValid("123", null));
  }

  @Test
  void shouldReject_WhenInvalidCharactersInPhone() {
    assertFalse(validator.isValid("+34-600-ABC-123", null));
  }

  @Test
  void shouldReject_WhenUrlMissingDomainExtension() {
    assertFalse(validator.isValid("http://miweb", null));
  }
}