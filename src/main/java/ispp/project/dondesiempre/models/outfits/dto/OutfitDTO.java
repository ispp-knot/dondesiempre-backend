package ispp.project.dondesiempre.models.outfits.dto;

import ispp.project.dondesiempre.models.outfits.Outfit;
import ispp.project.dondesiempre.models.outfits.OutfitProduct;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OutfitDTO {
  private Integer id;

  private String name;
  private String description;
  private String image;

  private Double price;
  private Double discount;

  private Integer index;
  private Integer storefrontId;

  private List<String> tags;
  private List<OutfitProductDTO> products;

  public OutfitDTO(Outfit outfit, List<String> tags, List<OutfitProduct> products) {
    this.id = outfit.getId();

    this.name = outfit.getName();
    this.description = outfit.getDescription();
    this.image = outfit.getImage();

    this.discount = outfit.getDiscount();

    this.index = outfit.getIndex();
    this.storefrontId = outfit.getStorefront().getId();

    this.tags = tags;
    this.products =
        products.stream()
            .map(product -> new OutfitProductDTO(product))
            .sorted(Comparator.comparing(product -> product.getIndex()))
            .collect(Collectors.toList());
    this.price = this.products.stream().mapToDouble(product -> product.getPrice()).sum();
  }
}
