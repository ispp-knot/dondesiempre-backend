package ispp.project.dondesiempre.models.outfits;

import ispp.project.dondesiempre.modules.common.models.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "outfit_tags")
public class OutfitTag extends BaseEntity {
  @Column
  @NotNull
  @Size(max = 255)
  private String name;
}
