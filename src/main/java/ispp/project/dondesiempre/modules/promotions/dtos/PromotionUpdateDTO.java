package ispp.project.dondesiempre.modules.promotions.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import ispp.project.dondesiempre.modules.promotions.validators.StartDateBeforeEndDate;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@StartDateBeforeEndDate
public class PromotionUpdateDTO {

  @Size(max = 255)
  private String name;

  @Min(1)
  @Max(100)
  private Integer discountPercentage;

  @JsonProperty("isActive")
  private boolean isActive;

  private List<UUID> productIds;

  private String description;

  private LocalDate startDate;

  private LocalDate endDate;
}
