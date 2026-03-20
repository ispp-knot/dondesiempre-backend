package ispp.project.dondesiempre.modules.promotions.models;

import ispp.project.dondesiempre.modules.common.models.BaseEntity;
import ispp.project.dondesiempre.modules.stores.models.Store;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Entity
@Getter
@Setter
@Table(name = "promotions")
public class Promotion extends BaseEntity {

  @Column
  @NotNull
  @Size(max = 255)
  private String name;

  @Column
  @NotNull
  @Min(1)
  @Max(100)
  private Integer discountPercentage;

  @Column @NotNull private boolean isActive;

  @ManyToOne(optional = false)
  @NotNull
  private Store store;

  @Column(name = "start_date")
  private LocalDate startDate;

  @Column(name = "end_date")
  private LocalDate endDate;

  @Column(columnDefinition = "TEXT")
  private String description;

  @URL
  @Column(name = "promotion_image_url")
  private String promotionImageUrl;

  public Optional<String> getDescription() {
    return Optional.ofNullable(this.description);
  }

  public Optional<String> getPromotionImageUrl() {
    return Optional.ofNullable(this.promotionImageUrl);
  }
}
