package ispp.project.dondesiempre.modules.products.dtos;

import ispp.project.dondesiempre.modules.products.models.Product;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductDTO {

  private UUID id;
  private String name;
  private Integer priceInCents;
  private Integer discountedPriceInCents;
  private String description;
  private String image;

  private UUID typeId;
  private UUID storeId;

  public ProductDTO(Product product) {
    this.setId(product.getId());
    this.setName(product.getName());
    this.setPriceInCents(product.getPriceInCents());
    this.setDiscountedPriceInCents(product.getDiscountedPriceInCents());
    this.setDescription(product.getDescription());
    this.setImage(product.getImage());
    this.setTypeId(product.getType().getId());
    this.setStoreId(product.getStore().getId());
  }
}
