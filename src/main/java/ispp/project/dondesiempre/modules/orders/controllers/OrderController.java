package ispp.project.dondesiempre.modules.orders.controllers;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.common.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.modules.orders.dtos.OrderDTO;
import ispp.project.dondesiempre.modules.orders.services.OrderService;
import ispp.project.dondesiempre.modules.products.models.Product;
import ispp.project.dondesiempre.modules.products.services.ProductService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;
  private final ProductService productService;

  @GetMapping
  public ResponseEntity<List<OrderDTO>> getMyOrders() {
    List<OrderDTO> orders = orderService.findOrdersOfCurrenUser();
    return new ResponseEntity<>(orders, HttpStatus.OK);
  }

  @GetMapping("/pick/{orderCode}")
  public ResponseEntity<OrderDTO> findOrder(@PathVariable String orderCode) 
          throws UnauthorizedException, ResourceNotFoundException {
      OrderDTO order = orderService.findOrder(orderCode);
      return new ResponseEntity<>(order, HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<OrderDTO> createOrder(
      @RequestBody Map<UUID, Integer> productIdsWithQuantity) {

    Map<Product, Integer> productsToBuy =
        productIdsWithQuantity.entrySet().stream()
            .collect(
                Collectors.toMap(
                    e -> productService.getProductById(e.getKey()), Map.Entry::getValue));

    OrderDTO response = orderService.createOrder(productsToBuy);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @PatchMapping("/{orderId}/confirm")
  public ResponseEntity<Void> confirmOrder(@PathVariable UUID orderId)
      throws UnauthorizedException {
    orderService.confirmOrder(orderId);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{orderId}/reject")
  public ResponseEntity<Void> rejectOrder(@PathVariable UUID orderId) throws UnauthorizedException {
    orderService.rejectOrder(orderId);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{orderId}/pick")
  public ResponseEntity<Void> pickOrder(@PathVariable UUID orderId) throws UnauthorizedException {
    orderService.pickOrder(orderId);
    return ResponseEntity.noContent().build();
  }
}
