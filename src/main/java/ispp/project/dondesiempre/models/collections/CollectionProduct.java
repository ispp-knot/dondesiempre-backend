package ispp.project.dondesiempre.models.collections;

import ispp.project.dondesiempre.models.products.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@Table(name = "collection_products")
@IdClass(CollectionProductId.class)
public class CollectionProduct {

  @Id
  @NotNull
  @ManyToOne(optional = false)
  @JoinColumn(name = "collection_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private ProductCollection collection;

  @Id
  @NotNull
  @ManyToOne(optional = false)
  @JoinColumn(name = "product_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Product product;
}
