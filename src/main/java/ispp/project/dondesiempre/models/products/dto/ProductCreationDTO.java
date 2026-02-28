package ispp.project.dondesiempre.models.products.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCreationDTO {

  private String name;
  private Integer priceInCents;
  private Integer discountedPriceInCents;
  private String description;
  private String image;
  private Integer typeId;
  private Integer storeId;
}
