package ispp.project.dondesiempre.dto.collection;

import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.models.products.ProductCollection;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CollectionResponseDTO {

  private Integer id;
  private String name;
  private String description;
  private Integer storeId;
  private String storeName;
  private List<Integer> productIds;

  public CollectionResponseDTO(ProductCollection collection) {
    this.id = collection.getId();
    this.name = collection.getName();
    this.description = collection.getDescription();
    this.storeId = collection.getStore().getId();
    this.storeName = collection.getStore().getName();
    this.productIds =
        collection.getProducts().stream().map(Product::getId).sorted(Integer::compareTo).toList();
  }
}
