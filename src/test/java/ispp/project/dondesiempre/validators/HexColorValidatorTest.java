package ispp.project.dondesiempre.validators;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class HexColorValidatorTest {

  private final HexColorValidator validator = new HexColorValidator();

  @Test
  void acceptsNull() {
    assertTrue(validator.isValid(null, null));
  }

  @Test
  void accepts6HexDigitsLowercase() {
    assertTrue(validator.isValid("#00ffaa", null));
  }

  @Test
  void accepts6HexDigitsUppercase() {
    assertTrue(validator.isValid("#AABBCC", null));
  }

  @Test
  void accepts3HexDigits() {
    assertTrue(validator.isValid("#0fA", null));
  }

  @Test
  void rejectsMissingHash() {
    assertFalse(validator.isValid("AABBCC", null));
  }

  @Test
  void rejectsTooShort() {
    assertFalse(validator.isValid("#AA", null));
  }

  @Test
  void rejectsTooLong() {
    assertFalse(validator.isValid("#AABBCCD", null));
  }

  @Test
  void rejectsNonHexCharacters() {
    assertFalse(validator.isValid("#GGHHII", null));
  }
}
