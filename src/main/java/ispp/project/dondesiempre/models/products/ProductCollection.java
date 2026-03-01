package ispp.project.dondesiempre.models.products;

import ispp.project.dondesiempre.models.BaseEntity;
import ispp.project.dondesiempre.models.stores.Store;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@Table(name = "collections")
public class ProductCollection extends BaseEntity {

  @Column
  @NotBlank
  @Size(max = 255)
  private String name;

  @Column(columnDefinition = "TEXT")
  @Size(max = 1000)
  private String description;

  @NotNull
  @ManyToOne(optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Store store;

  @ManyToMany
  @JoinTable(
      name = "collection_products",
      joinColumns = @JoinColumn(name = "collection_id"),
      inverseJoinColumns = @JoinColumn(name = "product_id"))
  @Setter(AccessLevel.NONE)
  private Set<Product> products = new HashSet<>();

  public void setProducts(Set<Product> products) {
    if (products == null) {
      this.products = new HashSet<>();
      return;
    }
    this.products = new HashSet<>(products);
  }
}
