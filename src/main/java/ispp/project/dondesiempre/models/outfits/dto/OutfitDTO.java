package ispp.project.dondesiempre.models.outfits.dto;

import ispp.project.dondesiempre.models.outfits.Outfit;
import ispp.project.dondesiempre.models.outfits.OutfitProduct;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OutfitDTO {
  private UUID id;

  private String name;
  private String description;
  private String image;

  private Integer priceInCents;
  private Integer discountedPriceInCents;

  private Integer index;
  private UUID storefrontId;

  private List<String> tags;
  private List<OutfitProductDTO> products;

  public static OutfitDTO from(Outfit outfit, List<String> tags, List<OutfitProduct> products) {
    OutfitDTO dto = new OutfitDTO();
    dto.id = outfit.getId();

    dto.name = outfit.getName();
    dto.description = outfit.getDescription();
    dto.image = outfit.getImage();

    dto.discountedPriceInCents = outfit.getDiscountedPriceInCents();

    dto.index = outfit.getIndex();
    dto.storefrontId = outfit.getStorefront().getId();

    dto.tags = tags;
    dto.products =
        products.stream()
            .map(OutfitProductDTO::from)
            .sorted(Comparator.comparing(OutfitProductDTO::getIndex))
            .toList();
    dto.priceInCents = dto.products.stream().mapToInt(OutfitProductDTO::getPriceInCents).sum();
    return dto;
  }
}
