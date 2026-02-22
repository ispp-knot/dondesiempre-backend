package ispp.project.dondesiempre.validators;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class HexColorValidatorTest {

  private final HexColorValidator validator = new HexColorValidator();

  @Test
  void shouldPass_WhenNull() {
    assertTrue(validator.isValid(null, null));
  }

  @Test
  void shouldAccept_When6HexDigitsLowercase() {
    assertTrue(validator.isValid("#00ffaa", null));
  }

  @Test
  void shouldAccept_When6HexDigitsUppercase() {
    assertTrue(validator.isValid("#AABBCC", null));
  }

  @Test
  void shouldAccept_When3HexDigits() {
    assertTrue(validator.isValid("#0fA", null));
  }

  @Test
  void shouldReject_WhenMissingHash() {
    assertFalse(validator.isValid("AABBCC", null));
  }

  @Test
  void shouldReject_WhenTooShort() {
    assertFalse(validator.isValid("#AA", null));
  }

  @Test
  void shouldReject_WhenTooLong() {
    assertFalse(validator.isValid("#AABBCCD", null));
  }

  @Test
  void shouldReject_WhenNonHexCharacters() {
    assertFalse(validator.isValid("#GGHHII", null));
  }
}
