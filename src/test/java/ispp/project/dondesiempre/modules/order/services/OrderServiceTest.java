package ispp.project.dondesiempre.modules.order.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.auth.repositories.UserRepository;
import ispp.project.dondesiempre.modules.auth.services.AuthService;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.common.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.modules.orders.dtos.OrderDTO;
import ispp.project.dondesiempre.modules.orders.models.Order;
import ispp.project.dondesiempre.modules.orders.models.OrderItem;
import ispp.project.dondesiempre.modules.orders.models.OrderStatus;
import ispp.project.dondesiempre.modules.orders.repositories.OrderItemRepository;
import ispp.project.dondesiempre.modules.orders.repositories.OrderRepository;
import ispp.project.dondesiempre.modules.orders.services.OrderService;
import ispp.project.dondesiempre.modules.products.models.Product;
import ispp.project.dondesiempre.modules.products.services.ProductService;
import ispp.project.dondesiempre.modules.stores.models.Store;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

  @Mock private OrderRepository orderRepository;
  @Mock private OrderItemRepository orderItemRepository;
  @Mock private ProductService productService;
  @Mock private AuthService authService;
  @Mock private UserRepository userRepository;
  @Mock private ApplicationContext applicationContext;

  @InjectMocks private OrderService orderService;

  private UUID orderId;
  private UUID itemId;
  private UUID storeId;
  private UUID userId;
  private UUID productId;

  private Order order;
  private OrderItem item;
  private Store store;
  private User user;
  private Product product;

  @BeforeEach
  void setUp() {
    orderId = UUID.randomUUID();
    itemId = UUID.randomUUID();
    storeId = UUID.randomUUID();
    userId = UUID.randomUUID();
    productId = UUID.randomUUID();

    user = new User();
    user.setId(userId);

    item = new OrderItem();
    item.setId(itemId);

    product = new Product();
    product.setId(productId);
    product.setName("Test Product");
    product.setPriceInCents(50);
    product.setStore(store);

    order = new Order();
    order.setId(orderId);
    order.setUser(user);
    order.setOrderDate(LocalDateTime.now());
    order.setOrderCode(this.orderService.generateRandomCode());
    List<OrderItem> items = new ArrayList<>();
    items.add(item);
    order.setItems(items);
    order.setTotalPrice(50);
    order.setOrderStatus(OrderStatus.PENDING);

    item.setOrder(order);
    item.setQuantity(1);
    item.setPriceAtPurchase(50);
    item.setProduct(product);

    lenient().when(applicationContext.getBean(OrderService.class)).thenReturn(orderService);
  }

  // find by id
  @Test
  void shouldReturnOrder_whenIdExists() throws ResourceNotFoundException {
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

    Order result = orderService.findById(orderId);

    assertNotNull(result);
    assertEquals(orderId, result.getId());
  }

  @Test
  void shouldThrowResourceNotFoundException_whenOrderDoesNotExist() {
    UUID nonExistentId = UUID.randomUUID();
    when(orderRepository.findById(nonExistentId)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> orderService.findById(nonExistentId));
  }

  // create
  @Test
  void shouldCreateOrder_whenValidData() {
    Map<Product, Integer> productsToBuy = Map.of(product, 2);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(orderRepository.save(any(Order.class))).thenReturn(order);

    OrderDTO result = orderService.createOrder(userId, productsToBuy);

    assertNotNull(result);
    assertEquals(userId, result.getUserId());
    assertEquals(50, result.getTotalPrice());
    assertTrue(result.getOrderCode().matches("^[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}$"));
    verify(orderRepository, times(1)).save(any(Order.class));
  }

  @Test
  void shouldResourceNotFoundException_whenUserNotFound() {
    Map<Product, Integer> productsToBuy = Map.of(product, 1);

    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> orderService.createOrder(userId, productsToBuy));
    verify(orderRepository, never()).save(any());
  }

  // order status

  @Test
  void shouldConfirmOrder_whenStatusIsPending() throws UnauthorizedException {
    order.setOrderStatus(OrderStatus.PENDING);
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

    orderService.confirmOrder(orderId);

    assertEquals(OrderStatus.ACCEPTED, order.getOrderStatus());
    verify(orderRepository, times(1)).findById(orderId);
  }

  @Test
  void shouldThrowUnauthorizedException_whenConfirmingNonPendingOrder() {
    order.setOrderStatus(OrderStatus.ACCEPTED);
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

    assertThrows(UnauthorizedException.class, () -> orderService.confirmOrder(orderId));
  }

  @Test
  void shouldRejectOrder_whenStatusIsPending() throws UnauthorizedException {
    order.setOrderStatus(OrderStatus.PENDING);
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

    orderService.rejectOrder(orderId);

    assertEquals(OrderStatus.REJECTED, order.getOrderStatus());
    verify(orderRepository, times(1)).findById(orderId);
  }

  @Test
  void shouldThrowUnauthorizedException_whenRejectingNonPendingOrder() {
    order.setOrderStatus(OrderStatus.REJECTED);
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

    assertThrows(UnauthorizedException.class, () -> orderService.rejectOrder(orderId));
  }

  @Test
  void shouldPickOrder_whenStatusIsAccepted() throws UnauthorizedException {
    order.setOrderStatus(OrderStatus.ACCEPTED);
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

    orderService.pickOrder(orderId);

    assertEquals(OrderStatus.PICKED, order.getOrderStatus());
    verify(orderRepository, times(1)).findById(orderId);
  }

  @Test
  void shouldThrowUnauthorizedException_whenPickingNonAcceptedOrder() {
    order.setOrderStatus(OrderStatus.PENDING);
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

    assertThrows(UnauthorizedException.class, () -> orderService.pickOrder(orderId));
  }

  // calculate total price
  @Test
  void shouldCalculateTotalPrice_whenOrderHasItems() {
    OrderItem item2 = new OrderItem();
    item2.setQuantity(3);
    item2.setPriceAtPurchase(500);

    order.setItems(List.of(item, item2));

    Integer result =
        ReflectionTestUtils.invokeMethod(orderService, "calculateAndSetTotalPrice", order);

    assertEquals(1550, result);
  }

  @Test
  void shouldReturnZero_whenOrderHasNoItems() {
    order.setItems(new ArrayList<>());

    Integer result =
        ReflectionTestUtils.invokeMethod(orderService, "calculateAndSetTotalPrice", order);

    assertEquals(0, result);
  }

  @Test
  void shouldReturnZero_whenItemsIsNull() {
    order.setItems(null);

    Integer result =
        ReflectionTestUtils.invokeMethod(orderService, "calculateAndSetTotalPrice", order);

    assertEquals(0, result);
  }
}
