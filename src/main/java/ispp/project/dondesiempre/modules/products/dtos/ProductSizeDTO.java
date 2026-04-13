package ispp.project.dondesiempre.modules.products.dtos;

import ispp.project.dondesiempre.modules.products.models.ProductSize;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductSizeDTO {

  private UUID id;
  private String name;

  public ProductSizeDTO(ProductSize size) {
    this.id = size.getId();
    this.name = size.getSize();
  }
}
