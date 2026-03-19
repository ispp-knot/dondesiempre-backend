package ispp.project.dondesiempre.modules.products.dtos;

import java.util.UUID;
import lombok.Data;

@Data
public class ProductUpdateDTO {
  private String name;
  private String description;
  private Integer priceInCents;
  private UUID productTypeId;
}
