package ispp.project.dondesiempre.modules.outfits.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OutfitCreationProductDTO {

  @NotNull private UUID productId;

  @NotNull
  @Min(0)
  private Integer index;
}
