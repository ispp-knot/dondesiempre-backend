package ispp.project.dondesiempre.modules.orders.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import ispp.project.dondesiempre.modules.orders.models.Order;
import ispp.project.dondesiempre.modules.orders.models.OrderItem;
import ispp.project.dondesiempre.modules.orders.models.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

  private UUID id;
  private String orderCode;
  private LocalDateTime orderDate;
  private OrderStatus orderStatus;
  private Integer totalPrice;
  private UUID userId;
  private String storeName;
  private List<OrderItemDTO> items;

  @JsonProperty("isPaid")
  private boolean isPaid;

  public OrderDTO(Order order) {
    this.id = order.getId();
    this.orderCode = order.getOrderCode();
    this.orderDate = order.getOrderDate();
    this.orderStatus = order.getOrderStatus();
    this.totalPrice = order.getTotalPrice();
    this.userId = order.getUser().getId();
    this.isPaid = order.getPaymentIntentId().isPresent();
    this.storeName =
        order.getItems() != null && !order.getItems().isEmpty()
            ? order.getItems().get(0).getProduct().getStore().getName()
            : null;
    this.items = order.getItems().stream().map(OrderItemDTO::new).toList();
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class OrderItemDTO {
    private UUID id;
    private UUID productId;
    private String productName;
    private UUID variantId;
    private String variantSize;
    private String variantColor;
    private Integer quantity;
    private Integer priceAtPurchase;
    private Integer subtotal;

    public OrderItemDTO(OrderItem orderItem) {
      this.id = orderItem.getId();
      this.productId = orderItem.getProduct().getId();
      this.productName = orderItem.getProduct().getName();
      this.quantity = orderItem.getQuantity();
      this.priceAtPurchase = orderItem.getPriceAtPurchase();
      this.subtotal = orderItem.getQuantity() * orderItem.getPriceAtPurchase();
      this.variantId = orderItem.getVariant().getId();
      this.variantSize = orderItem.getVariant().getSize().getSize();
      this.variantColor = orderItem.getVariant().getColor().getColor();
    }
  }
}
