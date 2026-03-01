package ispp.project.dondesiempre.models.products;

import ispp.project.dondesiempre.models.BaseEntity;
import ispp.project.dondesiempre.models.stores.Store;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
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

  @Column(columnDefinition = "TEXT")
  @Size(max = 5000)
  String description;

  @NotNull
  @ManyToOne(optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private ProductType type;

  @NotNull
  @ManyToOne(optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Store store;

  @ManyToMany(mappedBy = "products")
  private Set<ProductCollection> collections = new HashSet<>();
}
