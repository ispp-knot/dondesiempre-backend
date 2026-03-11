package ispp.project.dondesiempre.modules.promotions.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PromotionUpdateDTO {

  @Size(max = 255)
  private String name;

  @Min(1)
  @Max(100)
  private Integer discountPercentage;

  private boolean isActive;

  private List<UUID> productIds;

  private String description;
}
