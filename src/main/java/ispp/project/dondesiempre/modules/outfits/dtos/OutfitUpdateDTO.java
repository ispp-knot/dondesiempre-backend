package ispp.project.dondesiempre.modules.outfits.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OutfitUpdateDTO {
  private String name;
  private String description;

  private Integer discountedPriceInCents;

  private Integer index;
}
