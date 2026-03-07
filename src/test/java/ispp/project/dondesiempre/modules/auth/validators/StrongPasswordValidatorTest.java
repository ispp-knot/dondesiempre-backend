package ispp.project.dondesiempre.modules.auth.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StrongPasswordValidatorTest {

  private StrongPasswordValidator validator;

  @BeforeEach
  void setUp() {
    validator = new StrongPasswordValidator();
    validator.initialize(null);
  }

  @Test
  void isValid_shouldReturnTrue_whenPasswordMeetsAllRequirements() {
    assertTrue(validator.isValid("Password1!", null));
  }

  @Test
  void isValid_shouldReturnTrue_whenNull() {
    assertTrue(validator.isValid(null, null));
  }

  @Test
  void isValid_shouldReturnFalse_whenTooShort() {
    assertFalse(validator.isValid("Pass1!", null));
  }

  @Test
  void isValid_shouldReturnFalse_whenNoUppercase() {
    assertFalse(validator.isValid("password1!", null));
  }

  @Test
  void isValid_shouldReturnFalse_whenNoNumber() {
    assertFalse(validator.isValid("Password!", null));
  }

  @Test
  void isValid_shouldReturnFalse_whenNoSymbol() {
    assertFalse(validator.isValid("Password1", null));
  }
}
