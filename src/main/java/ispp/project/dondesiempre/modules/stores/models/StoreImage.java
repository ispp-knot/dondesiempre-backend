package ispp.project.dondesiempre.modules.stores.models;

import ispp.project.dondesiempre.modules.common.models.BaseEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.URL;

@Entity
@Getter
@Setter
@Table(name = "store_images")
public class StoreImage extends BaseEntity {
  @Column
  @NotNull
  @Min(0)
  @Max(4)
  private Integer displayOrder;

  @Column @Nullable @URL private String image;

  @ManyToOne(optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Store store;

  public Optional<String> getImage() {
    return Optional.ofNullable(this.image);
  }
}
