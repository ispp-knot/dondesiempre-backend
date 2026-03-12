package ispp.project.dondesiempre.modules.products.dtos;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DiscountModificationDTO {

  @Min(0)
  private Integer discountedPriceInCents;
}
