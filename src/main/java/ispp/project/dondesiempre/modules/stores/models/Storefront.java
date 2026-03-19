package ispp.project.dondesiempre.modules.stores.models;

import ispp.project.dondesiempre.modules.common.models.BaseEntity;
import ispp.project.dondesiempre.modules.stores.validators.HexColor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Entity
@Getter
@Setter
@Table(name = "storefronts")
public class Storefront extends BaseEntity {

  @HexColor
  @NotNull
  @Column(name = "primary_color")
  private String primaryColor = "#c65a3a";

  @HexColor
  @NotNull
  @Column(name = "secondary_color")
  private String secondaryColor = "#19756a";

  @URL
  @Column(name = "banner_image_url")
  private String bannerImageUrl;

  @OneToOne(mappedBy = "storefront")
  private Store store;

  public Optional<String> getBannerImageUrl() {
    return Optional.ofNullable(this.bannerImageUrl);
  }
}
