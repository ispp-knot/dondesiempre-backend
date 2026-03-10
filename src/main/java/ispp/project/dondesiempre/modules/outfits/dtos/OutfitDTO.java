package ispp.project.dondesiempre.modules.outfits.dtos;

import ispp.project.dondesiempre.modules.outfits.models.Outfit;
import ispp.project.dondesiempre.modules.outfits.models.OutfitProduct;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
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

  public OutfitDTO(Outfit outfit, List<String> tags, List<OutfitProduct> products) {
    this.id = outfit.getId();

    this.name = outfit.getName();
    this.description = outfit.getDescription().orElse(null);
    this.image = outfit.getImage().orElse(null);

    this.discountedPriceInCents = outfit.getDiscountedPriceInCents();

    this.index = outfit.getIndex();
    this.storefrontId = outfit.getStorefront().getId();

    this.tags = tags;
    this.products =
        products.stream()
            .map(OutfitProductDTO::new)
            .sorted(Comparator.comparing(OutfitProductDTO::getIndex))
            .toList();
    this.priceInCents =
        this.products.stream().mapToInt(p -> p.getProduct().getPriceInCents()).sum();
  }
}
