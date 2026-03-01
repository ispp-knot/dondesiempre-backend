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
  private String image;

  private Integer priceInCents;

  private ProductType type;

  private Integer index;
  private UUID storeId;

  public static OutfitProductDTO from(OutfitProduct product) {
    OutfitProductDTO dto = new OutfitProductDTO();
    dto.id = product.getProduct().getId();

    dto.name = product.getProduct().getName();
    dto.description = product.getProduct().getDescription();
    dto.image = product.getProduct().getImage();

    dto.priceInCents = product.getProduct().getDiscountedPriceInCents();
    dto.type = product.getProduct().getType();

    dto.index = product.getIndex();
    dto.storeId = product.getProduct().getStore().getId();
    return dto;
  }
}
