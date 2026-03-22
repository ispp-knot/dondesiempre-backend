package ispp.project.dondesiempre.modules.promotions.validators;

import java.time.LocalDate;

public interface HasDateRange {
  LocalDate getStartDate();

  LocalDate getEndDate();
}
