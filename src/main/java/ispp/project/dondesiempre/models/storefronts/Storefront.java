package ispp.project.dondesiempre.models.storefronts;

import ispp.project.dondesiempre.models.BaseEntity;
import ispp.project.dondesiempre.validators.HexColor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Entity
@Getter
@Setter
@Table(name = "storefronts")
public class Storefront extends BaseEntity {

  @NotNull
  @Column(name = "is_first_collections")
  private Boolean isFirstCollections = Boolean.TRUE;

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
}
