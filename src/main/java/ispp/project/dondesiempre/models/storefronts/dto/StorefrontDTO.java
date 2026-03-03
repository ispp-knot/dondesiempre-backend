package ispp.project.dondesiempre.models.storefronts.dto;

import ispp.project.dondesiempre.models.storefronts.Storefront;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
    this.bannerImageUrl = storefront.getBannerImageUrl();
  }

  public StorefrontDTO() {}
}
