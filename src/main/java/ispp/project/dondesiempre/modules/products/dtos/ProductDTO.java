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
  private Integer discountPercentage;
  private String description;
  private String image;

  private UUID typeId;
  private UUID storeId;

  public ProductDTO(Product product) {
    this.id = product.getId();
    this.name = product.getName();
    this.priceInCents = product.getPriceInCents();
    this.discountedPriceInCents =
        product.getPriceInCents()
            - (product.getDiscountPercentage().orElse(null) != null
                ? product.getPriceInCents() * product.getDiscountPercentage().get() / 100
                : 0);
    this.discountPercentage = product.getDiscountPercentage().orElse(null);
    this.description = product.getDescription().orElse(null);
    this.image = product.getImage().orElse(null);
    this.typeId = product.getType().getId();
    this.storeId = product.getStore().getId();
  }
}
