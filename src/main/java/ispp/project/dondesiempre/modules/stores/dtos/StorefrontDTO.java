package ispp.project.dondesiempre.modules.stores.dtos;

import ispp.project.dondesiempre.modules.stores.models.Storefront;
import ispp.project.dondesiempre.modules.stores.validators.HexColor;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

import org.hibernate.validator.constraints.URL;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StorefrontDTO {

  @NotNull
  private UUID id;

  private Boolean isFirstCollections;

  @HexColor
  private String primaryColor;

  @HexColor
  private String secondaryColor;

  @URL
  private String bannerImageUrl;

  public StorefrontDTO(Storefront storefront) {
    if (storefront == null)
      return;

    this.id = storefront.getId();
    this.isFirstCollections = storefront.getIsFirstCollections();
    this.primaryColor = storefront.getPrimaryColor();
    this.secondaryColor = storefront.getSecondaryColor();
    this.bannerImageUrl = storefront.getBannerImageUrl().orElse(null);
  }
}
