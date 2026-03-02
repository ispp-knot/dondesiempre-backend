package ispp.project.dondesiempre.models.collections;

import ispp.project.dondesiempre.models.BaseEntity;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.models.stores.Store;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

  @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CollectionProduct> collectionProducts = new ArrayList<>();

  public void setProducts(Set<Product> products) {
    collectionProducts.clear();
    if (products == null) {
      return;
    }
    products.forEach(this::addProduct);
  }

  public Set<Product> getProducts() {
    return collectionProducts.stream()
        .map(CollectionProduct::getProduct)
        .collect(java.util.stream.Collectors.toSet());
  }

  public void addProduct(Product product) {
    CollectionProduct collectionProduct = new CollectionProduct();
    collectionProduct.setCollection(this);
    collectionProduct.setProduct(product);
    collectionProducts.add(collectionProduct);
  }
}
