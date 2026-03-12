package ispp.project.dondesiempre.modules.outfits.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OutfitCreationDTO {

  @NotNull
  @Min(0)
  private Integer index;

  @NotNull
  @Size(max = 255)
  private String name;

  @Size(max = 5000)
  private String description;

  private List<String> tags;

  @NotEmpty @Valid private List<OutfitCreationProductDTO> products;
}
