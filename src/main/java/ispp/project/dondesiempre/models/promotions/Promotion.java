package ispp.project.dondesiempre.models.promotions;

import ispp.project.dondesiempre.models.BaseEntity;
import ispp.project.dondesiempre.models.stores.Store;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

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

  @Column(columnDefinition = "TEXT")
  private String description;
}
