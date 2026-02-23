package ispp.project.dondesiempre.models.outfits;

import ispp.project.dondesiempre.models.BaseEntity;
import ispp.project.dondesiempre.models.products.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@Table(name = "outfit_products")
public class OutfitProduct extends BaseEntity {
  @Column
  @NotNull
  @Min(0)
  private Integer index;

  @NotNull
  @ManyToOne(optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Outfit outfit;

  @NotNull
  @ManyToOne(optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Product product;
}
