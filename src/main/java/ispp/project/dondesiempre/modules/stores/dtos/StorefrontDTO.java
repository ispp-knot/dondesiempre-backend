package ispp.project.dondesiempre.modules.stores.dtos;

import ispp.project.dondesiempre.modules.stores.models.Storefront;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StorefrontDTO {

  private UUID id;
  private Boolean isFirstCollections;
  private String primaryColor;
  private String secondaryColor;
  private String bannerImageUrl;

  public StorefrontDTO(Storefront storefront) {
    if (storefront == null) return;

    this.id = storefront.getId();
    this.isFirstCollections = storefront.getIsFirstCollections();
    this.primaryColor = storefront.getPrimaryColor();
    this.secondaryColor = storefront.getSecondaryColor();
    this.bannerImageUrl = storefront.getBannerImageUrl().orElse(null);
  }
}
