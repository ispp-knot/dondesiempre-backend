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

  public OutfitDTO(Outfit outfit, List<String> tags, List<OutfitProduct> products) {
    this.id = outfit.getId();

    this.name = outfit.getName();
    this.description = outfit.getDescription();
    this.image = outfit.getImage();

    this.discountedPriceInCents = outfit.getDiscountedPriceInCents();

    this.index = outfit.getIndex();
    this.storefrontId = outfit.getStorefront().getId();

    this.tags = tags;
    this.products =
        products.stream()
            .map(product -> new OutfitProductDTO(product))
            .sorted(Comparator.comparing(product -> product.getIndex()))
            .toList();
    this.priceInCents = this.products.stream().mapToInt(product -> product.getPriceInCents()).sum();
  }
}
