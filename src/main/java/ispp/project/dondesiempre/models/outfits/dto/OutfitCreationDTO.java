package ispp.project.dondesiempre.models.outfits.dto;

import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OutfitCreationDTO {
  private Integer index;
  private UUID storefrontId;

  private String name;
  private String description;

  private List<String> tags;
  private List<OutfitCreationProductDTO> products;
}
