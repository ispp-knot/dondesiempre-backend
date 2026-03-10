package ispp.project.dondesiempre.modules.outfits.dtos;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OutfitCreationDTO {
  private Integer index;

  private String name;
  private String description;

  private List<String> tags;
  private List<OutfitCreationProductDTO> products;
}
