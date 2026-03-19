package ispp.project.dondesiempre.modules.products.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductUpdateDTO {

  @Size(max = 255)
  private String name;

  private String description;

  @Min(0)
  private Integer priceInCents;

  private UUID productTypeId;
}
