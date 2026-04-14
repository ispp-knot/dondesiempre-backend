package ispp.project.dondesiempre.modules.products.dtos;

import ispp.project.dondesiempre.modules.products.models.ProductVariant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductVariantDTO {

  private UUID id;
  private UUID productId;
  private UUID sizeId;
  private UUID colorId;
  private Boolean isAvailable;

  public ProductVariantDTO(ProductVariant variant) {
    this.id = variant.getId();
    this.productId = variant.getProduct().getId();
    this.sizeId = variant.getSize().getId();
    this.colorId = variant.getColor().getId();
    this.isAvailable = variant.getIsAvailable();
  }
}
