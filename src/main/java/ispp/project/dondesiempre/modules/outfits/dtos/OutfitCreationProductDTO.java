package ispp.project.dondesiempre.modules.outfits.dtos;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OutfitCreationProductDTO {
  private UUID productId;
  private Integer index;
}
