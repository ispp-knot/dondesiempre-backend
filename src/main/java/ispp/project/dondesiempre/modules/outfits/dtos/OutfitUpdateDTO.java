package ispp.project.dondesiempre.modules.outfits.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OutfitUpdateDTO {

  @Min(0)
  private Integer index;

  @Size(max = 255)
  private String name;

  @Size(max = 5000)
  private String description;

  @Min(0)
  private Integer discountedPriceInCents;

}
