package ispp.project.dondesiempre.models.promotions.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromotionCreationDTO {

  @NotNull
  @Size(max = 255)
  private String name;

  @NotNull
  @Min(1)
  @Max(100)
  private Integer discountPercentage;

  @NotNull private boolean isActive;

  @NotEmpty private List<Integer> productIds;

  @NotNull private Integer storeId;

  private String description;
}
