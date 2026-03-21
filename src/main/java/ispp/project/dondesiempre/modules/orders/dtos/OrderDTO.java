package ispp.project.dondesiempre.modules.orders.dtos;

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

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class OrderItemDTO {
    private UUID productId;
    private String productName;
    private Integer quantity;
    private Integer priceAtPurchase;
    private Integer subtotal;
  }
}
