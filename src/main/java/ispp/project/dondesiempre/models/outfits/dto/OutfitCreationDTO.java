package ispp.project.dondesiempre.models.outfits.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OutfitCreationDTO {
  private Integer index;

  private String name;
  private String image;

  private List<String> tags;
  private List<OutfitCreationProductDTO> products;
}
