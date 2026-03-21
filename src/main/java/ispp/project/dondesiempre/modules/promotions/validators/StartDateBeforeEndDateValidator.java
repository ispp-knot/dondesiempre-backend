package ispp.project.dondesiempre.modules.promotions.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StartDateBeforeEndDateValidator
    implements ConstraintValidator<StartDateBeforeEndDate, HasDateRange> {

  @Override
  public boolean isValid(HasDateRange dto, ConstraintValidatorContext context) {
    if (dto.getStartDate() == null || dto.getEndDate() == null) return true;
    return dto.getStartDate().isBefore(dto.getEndDate());
  }
}
