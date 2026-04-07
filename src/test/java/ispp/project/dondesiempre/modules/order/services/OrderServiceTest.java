package ispp.project.dondesiempre.modules.order.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
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
import ispp.project.dondesiempre.modules.orders.repositories.OrderRepository;
import ispp.project.dondesiempre.modules.orders.services.OrderService;
import ispp.project.dondesiempre.modules.outfits.models.Outfit;
import ispp.project.dondesiempre.modules.outfits.repositories.OutfitRepository;
import ispp.project.dondesiempre.modules.products.models.Product;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.repositories.StoreRepository;
import ispp.project.dondesiempre.utils.crypto.CryptoConverter;
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
  @Mock private UserRepository userRepository;
  @Mock private StoreRepository storeRepository;
  @Mock private AuthService authService;
  @Mock private CryptoConverter cryptoConverter;
  @Mock private ApplicationContext applicationContext;
  @Mock private OutfitRepository outfitRepository;

  @InjectMocks private OrderService orderService;

  private UUID orderId;
  private Order order;
  private User user;
  private Store store;
  private Product product;
  private OrderItem item;

  @BeforeEach
  void setUp() {
    orderId = UUID.randomUUID();

    user = new User();
    user.setId(UUID.randomUUID());

    store = new Store();
    store.setId(UUID.randomUUID());
    store.setName("Tienda Test");

    product = new Product();
    product.setStore(store);
    product.setPriceInCents(100);
    product.setName("Producto Test");

    item = new OrderItem();
    item.setProduct(product);
    item.setQuantity(2);
    item.setPriceAtPurchase(100);

    order = new Order();
    order.setId(orderId);
    order.setUser(user);
    order.setOrderStatus(OrderStatus.PENDING);
    order.setOrderDate(LocalDateTime.now());
    order.setItems(new ArrayList<>(List.of(item)));
    order.setOrderCode("CODE-1234-5678");
    order.setTotalPrice(200);
  }

  @Test
  void findAllOrders_ShouldReturnList() {
    when(orderRepository.findAll()).thenReturn(List.of(order));
    List<Order> result = orderService.findAllOrders();
    assertEquals(1, result.size());
    verify(orderRepository).findAll();
  }

  @Test
  void findOrdersOfCurrentUser_AsClient_ShouldReturnDTOs() {
    when(authService.getCurrentUser()).thenReturn(user);
    when(storeRepository.findByUserId(user.getId())).thenReturn(Optional.empty());
    when(orderRepository.findByUserId(user.getId())).thenReturn(List.of(order));

    List<OrderDTO> result = orderService.findOrdersOfCurrenUser();

    assertFalse(result.isEmpty());
    assertEquals("Tienda Test", result.get(0).getStoreName());
  }

  @Test
  void findOrdersOfCurrentUser_AsStore_ShouldReturnStoreOrders() {
    when(authService.getCurrentUser()).thenReturn(user);
    when(storeRepository.findByUserId(user.getId())).thenReturn(Optional.of(store));
    when(orderRepository.findByStoreId(store.getId())).thenReturn(List.of(order));

    List<OrderDTO> result = orderService.findOrdersOfCurrenUser();

    assertFalse(result.isEmpty());
    verify(orderRepository).findByStoreId(store.getId());
  }

  @Test
  void findOrdersByUserId_ShouldReturnList() {
    when(orderRepository.findByUserId(user.getId())).thenReturn(List.of(order));
    List<OrderDTO> result = orderService.findOrdersByUserId(user.getId());
    assertEquals(1, result.size());
  }

  @Test
  void findById_ExistingId_ShouldReturnOrder() throws ResourceNotFoundException {
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    Order result = orderService.findById(orderId);
    assertNotNull(result);
  }

  @Test
  void findById_NotExistingId_ShouldThrowException() {
    when(orderRepository.findById(any())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> orderService.findById(UUID.randomUUID()));
  }

  @Test
  void saveOrder_ShouldCalculateTotalAndSave() {
    when(orderRepository.save(any())).thenReturn(order);
    orderService.saveOrder(order);
    verify(orderRepository).save(order);
  }

  @Test
  void deleteOrder_ShouldInvokeRepository() {
    orderService.deleteOrder(orderId);
    verify(orderRepository).deleteById(orderId);
  }

  @Test
  void createOrder_ShouldCreateAndReturnDTO() {
    when(authService.getCurrentUser()).thenReturn(user);
    when(orderRepository.save(any())).thenReturn(order);
    Map<Product, Integer> products = Map.of(product, 2);

    OrderDTO result = orderService.createOrder(products, null);

    assertNotNull(result);
    verify(orderRepository).save(any(Order.class));
  }

  @Test
  void createOrder_WithOutfitDiscount_ShouldApplyDiscount() {
    UUID outfitId = UUID.randomUUID();
    Outfit outfit = new Outfit();
    outfit.setId(outfitId);
    outfit.setDiscountPercentage(20);

    when(authService.getCurrentUser()).thenReturn(user);
    when(outfitRepository.findById(outfitId)).thenReturn(Optional.of(outfit));
    when(orderRepository.save(any(Order.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Map<Product, Integer> products = Map.of(product, 2);

    OrderDTO result = orderService.createOrder(products, outfitId);

    assertNotNull(result);
    assertEquals(160, result.getTotalPrice());
    verify(orderRepository).save(any(Order.class));
  }

  @Test
  void createOrder_WithOutfitDiscount_ShouldTruncateCorrectly() {
    UUID outfitId = UUID.randomUUID();
    Outfit outfit = new Outfit();
    outfit.setId(outfitId);
    outfit.setDiscountPercentage(25);

    when(authService.getCurrentUser()).thenReturn(user);
    when(outfitRepository.findById(outfitId)).thenReturn(Optional.of(outfit));
    when(orderRepository.save(any(Order.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Product productTruncateTest = new Product();
    productTruncateTest.setStore(store);
    productTruncateTest.setPriceInCents(3998);
    productTruncateTest.setName("Producto Truncado");

    Map<Product, Integer> products = Map.of(productTruncateTest, 1);

    OrderDTO result = orderService.createOrder(products, outfitId);

    assertNotNull(result);
    assertEquals(2998, result.getTotalPrice());
  }

  @Test
  void confirmOrder_PendingStatus_ShouldWork() throws Exception {
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    orderService.confirmOrder(orderId);
    assertEquals(OrderStatus.CONFIRMED, order.getOrderStatus());
  }

  @Test
  void confirmOrder_NotPending_ShouldThrowException() {
    order.setOrderStatus(OrderStatus.CONFIRMED);
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    assertThrows(UnauthorizedException.class, () -> orderService.confirmOrder(orderId));
  }

  @Test
  void rejectOrder_PendingAndOwner_ShouldWork() throws Exception {
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    doNothing().when(authService).assertUserOwnsStore(any());

    orderService.rejectOrder(orderId);

    assertEquals(OrderStatus.REJECTED, order.getOrderStatus());
    verify(authService).assertUserOwnsStore(store);
  }

  @Test
  void findOrder_ByCode_ShouldReturnDTO() throws Exception {
    String plainCode = "CODE-1234";
    String encCode = "ENCRYPTED";
    when(cryptoConverter.convertToDatabaseColumn(plainCode)).thenReturn(encCode);
    when(orderRepository.findByOrderCode(encCode)).thenReturn(Optional.of(order));
    doNothing().when(authService).assertUserOwnsStore(any());

    OrderDTO result = orderService.findOrder(plainCode);

    assertNotNull(result);
    assertEquals("Tienda Test", result.getStoreName());
  }

  @Test
  void pickOrder_ConfirmedAndOwner_ShouldWork() throws Exception {
    order.setOrderStatus(OrderStatus.CONFIRMED);
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    doNothing().when(authService).assertUserOwnsStore(any());

    orderService.pickOrder(orderId);

    assertEquals(OrderStatus.PICKED, order.getOrderStatus());
  }

  @Test
  void cancelOrder_PendingAndOwner_ShouldWork() throws Exception {
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    doNothing().when(authService).assertUserOwnsStore(any());

    orderService.cancelOrder(orderId);

    assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());
  }

  @Test
  void calculateAndSetTotalPrice_LogicCheck() {
    Integer total =
        ReflectionTestUtils.invokeMethod(orderService, "calculateAndSetTotalPrice", order);
    assertEquals(200, total);
  }

  @Test
  void generateRandomCode_ShouldMatchFormat() {
    String code = orderService.generateRandomCode();
    assertTrue(code.matches("^[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}$"));
  }
}
