package ispp.project.dondesiempre.modules.outfits.dtos;

import ispp.project.dondesiempre.modules.outfits.models.OutfitProduct;
import ispp.project.dondesiempre.modules.products.models.ProductType;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OutfitProductDTO {
  private UUID id;

  private String name;
  private String description;
  private String image;

  private Integer priceInCents;

  private ProductType type;

  private Integer index;
  private UUID storeId;

  public OutfitProductDTO(OutfitProduct product) {
    this.id = product.getProduct().getId();

    this.name = product.getProduct().getName();
    this.description = product.getProduct().getDescription().orElse(null);
    this.image = product.getProduct().getImage().orElse(null);

    this.priceInCents = product.getProduct().getDiscountedPriceInCents();
    this.type = product.getProduct().getType();

    this.index = product.getIndex();
    this.storeId = product.getProduct().getStore().getId();
  }
}
