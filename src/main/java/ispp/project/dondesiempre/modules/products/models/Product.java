package ispp.project.dondesiempre.modules.products.models;

import ispp.project.dondesiempre.modules.common.models.BaseEntity;
import ispp.project.dondesiempre.modules.stores.models.Store;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.URL;

@Entity
@Getter
@Setter
@Table(name = "products")
public class Product extends BaseEntity {

  @Column
  @NotNull
  @Size(max = 255)
  String name;

  @Column
  @NotNull
  @Min(0)
  Integer priceInCents;

  @Column
  @Nullable
  @Min(1)
  @Max(100)
  Integer discountedPriceInCents;

  @Column(columnDefinition = "TEXT")
  @Size(max = 5000)
  String description;

  @Column @Nullable @URL private String image;

  @NotNull
  @ManyToOne(optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private ProductType type;

  @NotNull
  @ManyToOne(optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Store store;

  public Optional<String> getDescription() {
    return Optional.ofNullable(this.description);
  }

  public Optional<String> getImage() {
    return Optional.ofNullable(this.image);
  }

  public Optional<Integer> getDiscountedPriceInCents() {
    return Optional.ofNullable(this.discountedPriceInCents);
  }
}
