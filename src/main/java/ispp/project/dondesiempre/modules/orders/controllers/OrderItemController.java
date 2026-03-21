package ispp.project.dondesiempre.modules.orders.controllers;

import ispp.project.dondesiempre.modules.orders.dtos.OrderDTO.OrderItemDTO;
import ispp.project.dondesiempre.modules.orders.services.OrderItemService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order-items")
@RequiredArgsConstructor
public class OrderItemController {

  private final OrderItemService orderItemService;

  @GetMapping
  public ResponseEntity<List<OrderItemDTO>> getAllOrderItems() {
    List<OrderItemDTO> items = orderItemService.findAllOrderItems();
    return ResponseEntity.ok(items);
  }

  @GetMapping("/order/{orderId}")
  public ResponseEntity<List<OrderItemDTO>> getItemsByOrderId(@PathVariable UUID orderId) {
    List<OrderItemDTO> items = orderItemService.findItemsByOrderId(orderId);
    return ResponseEntity.ok(items);
  }

  @GetMapping("/product/{productId}")
  public ResponseEntity<List<OrderItemDTO>> getItemsByProductId(@PathVariable UUID productId) {
    List<OrderItemDTO> items = orderItemService.findItemsByProductId(productId);
    return ResponseEntity.ok(items);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteOrderItem(@PathVariable UUID id) {
    orderItemService.deleteOrderItem(id);
    return ResponseEntity.noContent().build();
  }
}
