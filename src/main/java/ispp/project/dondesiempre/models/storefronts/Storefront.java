package ispp.project.dondesiempre.models.storefronts;

import ispp.project.dondesiempre.models.BaseEntity;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.validators.HexColor;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "storefronts")
public class Storefront extends BaseEntity {

  @NotNull
  @Column(name = "is_first_colecciones")
  private Boolean isFirstColecciones = Boolean.TRUE;

  @HexColor
  @NotNull
  @Size(max = 255)
  @Column(name = "primary_color", length = 255)
  private String primaryColor = "#c65a3a";

  @HexColor
  @NotNull
  @Size(max = 255)
  @Column(name = "secondary_color", length = 255)
  private String secondaryColor = "#19756a";

  @Lob
  @Basic(fetch = FetchType.LAZY)
  @Column(name = "banner_image")
  private byte[] bannerImage;

  @Column(name = "banner_image_content_type", length = 255)
  private String bannerImageContentType;

  @Column(name = "banner_image_filename", length = 512)
  private String bannerImageFilename;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "store_id", referencedColumnName = "id")
  private Store store;
}
