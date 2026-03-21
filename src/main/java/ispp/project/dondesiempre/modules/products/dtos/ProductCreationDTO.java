package ispp.project.dondesiempre.modules.products.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductCreationDTO {

  @NotNull
  @Size(max = 255)
  private String name;

  @NotNull
  @Min(0)
  private Integer priceInCents;

  @Size(max = 5000)
  private String description;

  @NotNull private UUID typeId;
}
