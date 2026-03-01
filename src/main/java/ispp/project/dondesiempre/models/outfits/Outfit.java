package ispp.project.dondesiempre.models.outfits;

import ispp.project.dondesiempre.models.BaseEntity;
import ispp.project.dondesiempre.models.storefronts.Storefront;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.URL;

@Entity
@Getter
@Setter
@Table(name = "outfits")
public class Outfit extends BaseEntity {
  @Column
  @NotNull
  @Min(0)
  private Integer index;

  @Column
  @NotNull
  @Size(max = 255)
  private String name;

  @Column(columnDefinition = "TEXT")
  @Nullable
  @Size(max = 5000)
  private String description;

  @Column @Nullable @URL private String image;

  @Column
  @NotNull
  @Min(0)
  private Integer discountedPriceInCents;

  @NotNull
  @ManyToOne(optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Storefront storefront;
}
