package ispp.project.dondesiempre.dto.category;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryResponseDTO {

  private Integer id;
  private String name;
  private String description;
  private Integer storeId;
  private String storeName;
}
