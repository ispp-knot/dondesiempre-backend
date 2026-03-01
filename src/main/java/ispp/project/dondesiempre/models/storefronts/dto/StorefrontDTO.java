package ispp.project.dondesiempre.models.storefronts.dto;

import ispp.project.dondesiempre.models.storefronts.Storefront;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StorefrontDTO {
  private Boolean isFirstCollections = Boolean.TRUE;
  private String primaryColor = "#c65a3a";
  private String secondaryColor = "#19756a";
  private String bannerImageUrl;

  public static StorefrontDTO fromStorefront(Storefront storefront) {
    StorefrontDTO dto = new StorefrontDTO();
    dto.setIsFirstCollections(storefront.getIsFirstCollections());
    dto.setPrimaryColor(storefront.getPrimaryColor());
    dto.setSecondaryColor(storefront.getSecondaryColor());
    dto.setBannerImageUrl(storefront.getBannerImageUrl());
    return dto;
  }
}
