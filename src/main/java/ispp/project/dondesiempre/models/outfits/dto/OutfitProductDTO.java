package ispp.project.dondesiempre.models.outfits.dto;

import ispp.project.dondesiempre.models.outfits.OutfitProduct;
import ispp.project.dondesiempre.models.products.ProductType;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OutfitProductDTO {
  private UUID id;

  private String name;
  private String description;

  private Integer priceInCents;

  private ProductType type;

  private Integer index;
  private UUID storeId;

  public OutfitProductDTO(OutfitProduct product) {
    this.id = product.getId();

    this.name = product.getProduct().getName();
    this.description = product.getProduct().getDescription();

    this.priceInCents = product.getProduct().getDiscountedPriceInCents();
    this.type = product.getProduct().getType();

    this.index = product.getIndex();
    this.storeId = product.getProduct().getStore().getId();
  }
}
