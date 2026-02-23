package ispp.project.dondesiempre.models.products.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCreationDTO {

  private String name;
  private Integer priceEuros;
  private Integer priceCents;
  private Integer discountEuros;
  private Integer discountCents;
  private String description;
  private Integer typeId;
}
