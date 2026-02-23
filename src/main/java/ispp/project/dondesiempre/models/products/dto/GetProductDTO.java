package ispp.project.dondesiempre.models.products.dto;

import ispp.project.dondesiempre.models.products.Product;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetProductDTO {

  private String name;
  private Integer priceInCents;
  private Integer discountedPriceInCents;
  private String description;
  private Integer typeId;

  public static GetProductDTO fromProduct(Product product) {
    GetProductDTO dto = new GetProductDTO();
    dto.setName(product.getName());
    dto.setPriceInCents(product.getPriceInCents());
    dto.setDiscountedPriceInCents(product.getDiscountedPriceInCents());
    dto.setDescription(product.getDescription());
    dto.setTypeId(product.getType().getId());
    return dto;
  }
}
