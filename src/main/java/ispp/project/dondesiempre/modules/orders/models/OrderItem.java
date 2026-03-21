package ispp.project.dondesiempre.modules.orders.models;

import ispp.project.dondesiempre.modules.common.models.BaseEntity;
import ispp.project.dondesiempre.modules.products.models.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "order_items")
public class OrderItem extends BaseEntity {

  @ManyToOne(optional = false)
  @JoinColumn(name = "order_id", referencedColumnName = "id", nullable = false)
  private Order order;

  @ManyToOne(optional = false)
  @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false)
  private Product product;

  @Column(nullable = false)
  private Integer quantity;

  @Column(nullable = false)
  private Integer priceAtPurchase;
}
