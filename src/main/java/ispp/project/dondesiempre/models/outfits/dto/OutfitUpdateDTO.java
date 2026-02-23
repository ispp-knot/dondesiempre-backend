package ispp.project.dondesiempre.models.outfits.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OutfitUpdateDTO {
  private String name;
  private String description;
  private String image;

  private BigDecimal discount;

  private Integer index;
}
