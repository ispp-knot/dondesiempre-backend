package ispp.project.dondesiempre.models.products.dto;

import ispp.project.dondesiempre.models.products.Product;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {

  private String name;
  private Integer priceInCents;
  private Integer discountedPriceInCents;
  private String description;
  private UUID typeId;
  private UUID storeId;

  public static ProductDTO fromProduct(Product product) {
    ProductDTO dto = new ProductDTO();
    dto.setName(product.getName());
    dto.setPriceInCents(product.getPriceInCents());
    dto.setDiscountedPriceInCents(product.getDiscountedPriceInCents());
    dto.setDescription(product.getDescription());
    dto.setTypeId(product.getType().getId());
    dto.setStoreId(product.getStore().getId());
    return dto;
  }
}
