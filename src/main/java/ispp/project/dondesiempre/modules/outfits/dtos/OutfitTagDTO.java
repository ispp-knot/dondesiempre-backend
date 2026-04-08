package ispp.project.dondesiempre.modules.outfits.dtos;

import ispp.project.dondesiempre.modules.outfits.validators.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OutfitTagDTO {

  @Tag private String name;

  public OutfitTagDTO(String name) {
    this.name = name;
  }
}
