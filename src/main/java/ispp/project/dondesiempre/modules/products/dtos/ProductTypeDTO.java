package ispp.project.dondesiempre.modules.products.dtos;

import ispp.project.dondesiempre.modules.products.models.ProductType;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductTypeDTO {

  private UUID id;
  private String name;

  public ProductTypeDTO(ProductType type) {
    this.id = type.getId();
    this.name = type.getType();
  }
}
