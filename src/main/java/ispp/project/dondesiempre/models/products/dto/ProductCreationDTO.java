package ispp.project.dondesiempre.models.products.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCreationDTO {

  private String name;
  private Double price;
  private Double discount;
  private String description;
  private Integer typeId;
}
