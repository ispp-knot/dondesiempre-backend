package ispp.project.dondesiempre.dto.category;

import ispp.project.dondesiempre.models.products.ProductCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CategoryResponseDTO {

  private Integer id;
  private String name;
  private String description;
  private Integer storeId;
  private String storeName;

  public CategoryResponseDTO(ProductCategory category) {
    this.id = category.getId();
    this.name = category.getName();
    this.description = category.getDescription();
    this.storeId = category.getStore().getId();
    this.storeName = category.getStore().getName();
  }
}
