package ispp.project.dondesiempre.modules.products.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductDiscountUpdateDTO {

  @Min(1)
  @Max(100)
  private Integer discountPercentage;
}
