package ispp.project.dondesiempre.modules.outfits.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OutfitSortDTO {
  @NotNull private UUID id;

  @NotNull
  @Min(0)
  private Integer index;
}
