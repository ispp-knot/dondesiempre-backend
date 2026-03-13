package ispp.project.dondesiempre.modules.products.dtos;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductCreationDTO {

  private String name;
  private Integer priceInCents;
  private Integer discountedPriceInCents;
  private String description;

  private UUID typeId;
}
