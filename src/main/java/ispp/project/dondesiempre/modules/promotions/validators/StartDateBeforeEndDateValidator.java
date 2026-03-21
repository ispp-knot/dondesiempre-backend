package ispp.project.dondesiempre.modules.promotions.validators;

import ispp.project.dondesiempre.modules.promotions.dtos.PromotionUpdateDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StartDateBeforeEndDateValidator
    implements ConstraintValidator<StartDateBeforeEndDate, PromotionUpdateDTO> {

  @Override
  public boolean isValid(PromotionUpdateDTO dto, ConstraintValidatorContext context) {
    if (dto.getStartDate() == null || dto.getEndDate() == null) return true;
    return dto.getStartDate().isBefore(dto.getEndDate());
  }
}
