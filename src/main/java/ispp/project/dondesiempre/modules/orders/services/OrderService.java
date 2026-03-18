package ispp.project.dondesiempre.modules.orders.services;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.auth.repositories.UserRepository;
import ispp.project.dondesiempre.modules.auth.services.AuthService;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.common.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.modules.orders.dtos.OrderDTO;
import ispp.project.dondesiempre.modules.orders.models.Order;
import ispp.project.dondesiempre.modules.orders.models.OrderItem;
import ispp.project.dondesiempre.modules.orders.models.OrderStatus;
import ispp.project.dondesiempre.modules.orders.repositories.OrderRepository;
import ispp.project.dondesiempre.modules.stores.repositories.StoreRepository;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.products.models.Product;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final UserRepository userRepository;
  private final StoreRepository storeRepository;
  private final AuthService authService;

  private final SecureRandom secureRandom = new SecureRandom();
  private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

  @Transactional(readOnly = true)
  public List<Order> findAllOrders() {
    return orderRepository.findAll();
  }

  @Transactional(readOnly = true)
  public List<OrderDTO> findOrdersOfCurrenUser() {
    User currentUser = authService.getCurrentUser();
    if (currentUser == null) { 
      throw new UnauthorizedException("Debes iniciar sesión para ver tus pedidos."); 
    }

    Optional<Store> userStore = storeRepository.findByUserId(currentUser.getId());

    List<Order> orders;
    if (userStore.isPresent()) {
      orders = orderRepository.findByStoreId(userStore.get().getId());
    } else {
      orders = orderRepository.findByUserId(currentUser.getId());
    }

    return orders.stream().map(this::mapToOrderDTO).toList();
  }

  @Transactional(readOnly = true)
  public List<OrderDTO> findOrdersByUserId(UUID userId) {
    return orderRepository.findByUserId(userId).stream().map(this::mapToOrderDTO).toList();
  }

  @Transactional(readOnly = true, rollbackFor = ResourceNotFoundException.class)
  public Order findById(UUID id) throws ResourceNotFoundException {
    return orderRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + id + "not found."));
  }

  @Transactional
  public Order saveOrder(Order order) {
    order.setTotalPrice(this.calculateAndSetTotalPrice(order));
    return orderRepository.save(order);
  }

  @Transactional
  public void deleteOrder(UUID orderId) {
    orderRepository.deleteById(orderId);
  }

  public String generateRandomCode() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 12; i++) {
      if (i > 0 && i % 4 == 0) {
        sb.append("-");
      }
      int index = secureRandom.nextInt(CHARS.length());
      sb.append(CHARS.charAt(index));
    }
    return sb.toString();
  }

  private OrderDTO mapToOrderDTO(Order order) {
    return OrderDTO.builder()
        .id(order.getId())
        .orderCode(order.getOrderCode())
        .orderDate(order.getOrderDate())
        .orderStatus(order.getOrderStatus())
        .totalPrice(order.getTotalPrice())
        .userId(order.getUser().getId())
        .items(order.getItems().stream().map(this::mapItemToDTO).toList())
        .build();
  }

  private OrderDTO.OrderItemDTO mapItemToDTO(OrderItem item) {
    return OrderDTO.OrderItemDTO.builder()
        .productId(item.getProduct().getId())
        .productName(item.getProduct().getName())
        .quantity(item.getQuantity())
        .priceAtPurchase(item.getPriceAtPurchase())
        .subtotal(item.getQuantity() * item.getPriceAtPurchase())
        .build();
  }

  @Transactional(rollbackFor = ResourceNotFoundException.class)
  public OrderDTO createOrder(Map<Product, Integer> productsToBuy) {
    User user = authService.getCurrentUser();

    Order order = new Order();
    order.setUser(user);
    order.setOrderDate(LocalDateTime.now());
    order.setOrderStatus(OrderStatus.PENDING);
    order.setOrderCode(this.generateRandomCode());

    for (Map.Entry<Product, Integer> entry : productsToBuy.entrySet()) {
      OrderItem item = new OrderItem();
      item.setOrder(order);
      item.setProduct(entry.getKey());
      item.setQuantity(entry.getValue());
      item.setPriceAtPurchase(entry.getKey().getPriceInCents());

      order.getItems().add(item);
    }

    order.setTotalPrice(this.calculateAndSetTotalPrice(order));
    Order savedOrder = orderRepository.save(order);

    return mapToOrderDTO(savedOrder);
  }

  @Transactional
  public void confirmOrder(UUID orderId) throws UnauthorizedException, ResourceNotFoundException {
    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Order with ID" + orderId + "not found"));
    if (order.getOrderStatus().equals(OrderStatus.PENDING)) {
      order.setOrderStatus(OrderStatus.CONFIRMED);
    } else {
      throw new UnauthorizedException(
          "This order is not in the pending state, it cannot be confirmed.");
    }
  }

  @Transactional
  public void rejectOrder(UUID orderId) throws UnauthorizedException, ResourceNotFoundException {
    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Order with ID" + orderId + "not found"));
    if (order.getOrderStatus().equals(OrderStatus.PENDING)) {
      order.setOrderStatus(OrderStatus.REJECTED);
    } else {
      throw new UnauthorizedException(
          "This order is not in the pending state, it cannot be rejected.");
    }
  }

  @Transactional
  public void pickOrder(UUID orderId) throws UnauthorizedException, ResourceNotFoundException {
    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Order with ID" + orderId + "not found"));
    if (order.getOrderStatus().equals(OrderStatus.CONFIRMED)) {
      order.setOrderStatus(OrderStatus.PICKED);
    } else {
      throw new UnauthorizedException(
          "This order is not in the CONFIRMED state, it cannot be in the picked state.");
    }
  }

  // Este método se puede usar para cancelar un pedido que aún no ha sido confirmado.
  /**
   * TODO: Podría añadirse que un pedido sea cancelado por un cliente o una tienda, si y solo si: -
   * Cliente: puede cancelar un pedido si está PENDING (se ha equivocado, se ha dado cuenta que no
   * quería X item, etc.) - Tienda: puede cancelar un pedido si está CONFIRMED (se han dado cuenta
   * que no queda stock, el pedido es imposible de preparar, etc.) Ahora mismo no está teniendo en
   * cuenta estos roles.
   */
  @Transactional
  public void cancelOrder(UUID orderId) throws UnauthorizedException, ResourceNotFoundException {
    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Order with ID" + orderId + "not found"));
    if (order.getOrderStatus().equals(OrderStatus.PENDING)) {
      order.setOrderStatus(OrderStatus.CANCELLED);
    } else {
      throw new UnauthorizedException(
          "This order is not in the pending state, it cannot be canceled.");
    }
  }

  private Integer calculateAndSetTotalPrice(Order order) {
    Integer total = 0;
    if (order.getItems() != null && !order.getItems().isEmpty()) {
      total =
          order.getItems().stream().mapToInt(i -> i.getQuantity() * i.getPriceAtPurchase()).sum();
    }
    return total;
  }
}
