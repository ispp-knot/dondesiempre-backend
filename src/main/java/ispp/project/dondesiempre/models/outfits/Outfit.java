package ispp.project.dondesiempre.models.outfits;

import ispp.project.dondesiempre.models.BaseEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "outfits")
public class Outfit extends BaseEntity {
  @Column
  @NotNull
  @Min(0)
  private Integer order;

  @Column
  @NotNull
  @Size(max = 255)
  private String name;

  @Column
  @Nullable
  @Size(max = 255)
  private String image;

  @Column
  @Nullable
  @Min(0)
  private Double discount;
}
