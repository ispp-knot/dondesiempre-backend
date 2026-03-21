package ispp.project.dondesiempre.modules.products.dtos;

import ispp.project.dondesiempre.modules.products.models.ProductType;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductTypeDTO {

  private UUID id;

  @Size(max = 255)
  private String name;

  public ProductTypeDTO(ProductType type) {
    this.id = type.getId();
    this.name = type.getType();
  }
}
