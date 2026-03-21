package ispp.project.dondesiempre.modules.outfits.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OutfitUpdateDTO {

  @Size(max = 255)
  private String name;

  @Size(max = 5000)
  private String description;

  @Min(0)
  @Max(100)
  private Integer discountPercentage;
}
