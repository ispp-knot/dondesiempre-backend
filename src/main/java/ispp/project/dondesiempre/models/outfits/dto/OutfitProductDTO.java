package ispp.project.dondesiempre.models.outfits.dto;

import ispp.project.dondesiempre.models.outfits.OutfitProduct;
import ispp.project.dondesiempre.models.products.ProductType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OutfitProductDTO {
  private Integer id;

  private String name;
  private String description;

  private Double price;

  private ProductType type;
  private Integer storeId;

  public OutfitProductDTO(OutfitProduct product) {
    this.id = product.getId();

    this.name = product.getProduct().getName();
    this.description = product.getProduct().getDescription();

    this.price = product.getProduct().getPrice();
    this.type = product.getProduct().getType();

    this.storeId = product.getProduct().getStore().getId();
  }
}
