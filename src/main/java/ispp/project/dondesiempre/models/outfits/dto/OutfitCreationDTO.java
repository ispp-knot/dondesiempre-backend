package ispp.project.dondesiempre.models.outfits.dto;

import java.util.UUID;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OutfitCreationDTO {
  private Integer index;
  private UUID storefrontId;

  private String name;
  private String description;
  private String image;

  private List<String> tags;
  private List<OutfitCreationProductDTO> products;
}
