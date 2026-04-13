package ispp.project.dondesiempre.modules.products.dtos;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductVariantCreationDTO {

  @NotNull private UUID productId;

  @NotNull private UUID sizeId;

  @NotNull private UUID colorId;

  @NotNull private Boolean isAvailable;
}
