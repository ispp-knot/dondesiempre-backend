package ispp.project.dondesiempre.models.products;

import ispp.project.dondesiempre.models.BaseEntity;
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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
  Double price;

  @Column
  @NotNull
  @Min(0)
  @Max(1)
  Double discount;

  @Column(columnDefinition = "TEXT")
  @Size(max = 5000)
  String description;

  @NotNull
  @ManyToOne(optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Type type;
}
