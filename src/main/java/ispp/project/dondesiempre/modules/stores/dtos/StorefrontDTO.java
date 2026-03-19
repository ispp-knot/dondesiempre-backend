package ispp.project.dondesiempre.modules.stores.dtos;

import ispp.project.dondesiempre.modules.stores.models.Storefront;
import ispp.project.dondesiempre.modules.stores.validators.HexColor;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@NoArgsConstructor
public class StorefrontDTO {

  private UUID id;

  @HexColor private String primaryColor;

  @HexColor private String secondaryColor;

  @URL private String bannerImageUrl;

  public StorefrontDTO(Storefront storefront) {
    if (storefront == null) return;

    this.id = storefront.getId();
    this.primaryColor = storefront.getPrimaryColor();
    this.secondaryColor = storefront.getSecondaryColor();
    this.bannerImageUrl = storefront.getBannerImageUrl().orElse(null);
  }
}
