package ispp.project.dondesiempre.modules.promotions.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ispp.project.dondesiempre.modules.promotions.models.Promotion;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class StartDateBeforeEndDateValidatorTest {

  private static StartDateBeforeEndDateValidator validator;
  private static Validator validatorBean;

  @BeforeAll
  static void setUp() {
    validator = new StartDateBeforeEndDateValidator();
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validatorBean = factory.getValidator();
  }

  private Promotion basePromotion() {
    Promotion p = new Promotion();
    p.setName("Promo test");
    p.setDiscountPercentage(15);
    p.setActive(true);
    return p;
  }

  @Test
  void shouldBeValid_whenNullDates() {
    Promotion p = basePromotion();
    assertTrue(validator.isValid(p, null));
  }

  @Test
  void shouldPass_whenStartIsBeforeEnd() {
    Promotion p = basePromotion();
    p.setStartDate(LocalDate.now());
    p.setEndDate(LocalDate.now().plusDays(10));
    assertTrue(validator.isValid(p, null));
  }

  @Test
  void shouldBeInvalid_whenSameDate() {
    Promotion p = basePromotion();
    LocalDate same = LocalDate.now().plusDays(5);
    p.setStartDate(same);
    p.setEndDate(same);
    assertFalse(validator.isValid(p, null));
  }

  @Test
  void shouldBeInvalid_whenStartAfterEnd() {
    Promotion p = basePromotion();
    p.setStartDate(LocalDate.now().plusDays(10));
    p.setEndDate(LocalDate.now().plusDays(3));
    assertFalse(validator.isValid(p, null));
  }

  @Test
  void shouldFailValidation_whenStartDateIsAfterEndDate() {
    Promotion p = basePromotion();
    p.setStartDate(LocalDate.now().plusDays(10));
    p.setEndDate(LocalDate.now().plusDays(3));

    Set<ConstraintViolation<Promotion>> violations = validatorBean.validate(p);

    assertTrue(
        violations.stream()
            .anyMatch(
                v ->
                    v.getMessage()
                        .equals("La fecha de fin debe ser posterior a la fecha de inicio")));
  }

  @Test
  void shouldFailValidation_whenEndDateIsInThePast() {
    Promotion p = basePromotion();
    p.setEndDate(LocalDate.now().minusDays(1));

    Set<ConstraintViolation<Promotion>> violations = validatorBean.validate(p);

    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("endDate")));
  }
}
