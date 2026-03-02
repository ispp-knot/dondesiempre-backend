package ispp.project.dondesiempre.models.outfits.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OutfitUpdateDTO {
  private String name;
  private String description;

  private Integer discountedPriceInCents;

  private Integer index;
}
