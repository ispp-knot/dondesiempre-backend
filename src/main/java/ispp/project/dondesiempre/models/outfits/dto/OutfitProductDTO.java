package ispp.project.dondesiempre.models.outfits.dto;

import ispp.project.dondesiempre.models.outfits.OutfitProduct;
import ispp.project.dondesiempre.models.products.ProductType;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OutfitProductDTO {
  private Integer id;

  private String name;
  private String description;

  private BigDecimal price;

  private ProductType type;

  private Integer index;
  private Integer storeId;

  public OutfitProductDTO(OutfitProduct product) {
    this.id = product.getId();

    this.name = product.getProduct().getName();
    this.description = product.getProduct().getDescription();

    this.price = new BigDecimal(product.getProduct().getPrice());
    this.type = product.getProduct().getType();

    this.index = product.getIndex();
    this.storeId = product.getProduct().getStore().getId();
  }
}
