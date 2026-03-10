package ispp.project.dondesiempre.modules.outfits.dtos;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import ispp.project.dondesiempre.modules.outfits.models.OutfitProduct;
import ispp.project.dondesiempre.modules.products.dtos.ProductDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OutfitProductDTO {

  private Integer index;

  public OutfitProductDTO(OutfitProduct product) {
    this.id = product.getProduct().getId();

    this.name = product.getProduct().getName();
    this.description = product.getProduct().getDescription().orElse(null);
    this.image = product.getProduct().getImage().orElse(null);

    this.priceInCents = product.getProduct().getDiscountedPriceInCents();
    this.type = product.getProduct().getType();
    @JsonUnwrapped private ProductDTO product;


  public OutfitProductDTO(OutfitProduct product) {
    this.index = product.getIndex();
    this.product = new ProductDTO(product.getProduct());
  }
}
