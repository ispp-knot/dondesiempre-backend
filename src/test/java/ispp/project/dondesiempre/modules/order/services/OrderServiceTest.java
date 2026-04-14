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
import ispp.project.dondesiempre.modules.outfits.services.OutfitService;
import ispp.project.dondesiempre.modules.payment.services.StripeVerificationService;
import ispp.project.dondesiempre.modules.products.models.Product;
import ispp.project.dondesiempre.modules.products.models.ProductColor;
import ispp.project.dondesiempre.modules.products.models.ProductSize;
import ispp.project.dondesiempre.modules.products.models.ProductType;
import ispp.project.dondesiempre.modules.products.models.ProductVariant;
import ispp.project.dondesiempre.modules.products.services.ProductVariantService;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.repositories.StoreRepository;
import ispp.project.dondesiempre.utils.crypto.CryptoConverter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
  @Mock private ProductVariantService productVariantService;
  @Mock private CryptoConverter cryptoConverter;
  @Mock private ApplicationContext applicationContext;
  @Mock private OutfitService outfitService;
  @Mock private StripeVerificationService stripeVerificationService;

  @InjectMocks private OrderService orderService;

  private UUID orderId;
  private UUID variantId;
  private Order order;
  private User user;
  private Store store;
  private Product product;
  private ProductVariant variant;
  private OrderItem item;

  @BeforeEach
  void setUp() {
    orderId = UUID.randomUUID();
    variantId = UUID.randomUUID();

    user = new User();
    user.setId(UUID.randomUUID());

    store = new Store();
    store.setId(UUID.randomUUID());
    store.setName("Tienda Test");

    product = new Product();
    product.setStore(store);
    product.setPriceInCents(100);
    product.setName("Producto Test");

    ProductSize size = new ProductSize();
    size.setSize("M");

    ProductColor color = new ProductColor();
    color.setColor("Red");

    variant = new ProductVariant();
    variant.setId(variantId);
    variant.setProduct(product);
    variant.setSize(size);
    variant.setColor(color);
    variant.setIsAvailable(true);

    item = new OrderItem();
    item.setProduct(product);
    item.setVariant(variant);
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
  void createOrder_EmptyItems_ShouldThrowException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> orderService.createOrder(Collections.emptyMap(), null));
  }

  @Test
  void createOrder_NegativeQuantity_ShouldThrowException() {
    Map<UUID, Integer> items = new HashMap<>();
    items.put(variantId, -1);
    assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(items, null));
  }

  @Test
  void createOrder_ZeroQuantity_ShouldThrowException() {
    Map<UUID, Integer> items = new HashMap<>();
    items.put(variantId, 0);
    assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(items, null));
  }

  @Test
  void createOrder_DifferentStores_ShouldThrowException() throws ResourceNotFoundException {
    UUID variantId2 = UUID.randomUUID();
    Store store2 = new Store();
    store2.setId(UUID.randomUUID());

    Product product2 = new Product();
    product2.setStore(store2);

    ProductVariant variant2 = new ProductVariant();
    variant2.setId(variantId2);
    variant2.setProduct(product2);
    variant2.setIsAvailable(true);

    when(authService.getCurrentUser()).thenReturn(user);
    when(productVariantService.getProductVariantById(variantId)).thenReturn(variant);
    when(productVariantService.getProductVariantById(variantId2)).thenReturn(variant2);

    Map<UUID, Integer> items = new HashMap<>();
    items.put(variantId, 1);
    items.put(variantId2, 1);

    assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(items, null));
  }

  @Test
  void findAllOrders_ShouldReturnList() {
    when(orderRepository.findAll()).thenReturn(List.of(order));
    List<Order> result = orderService.findAllOrders();
    assertEquals(1, result.size());
    verify(orderRepository).findAll();
  }

  @Test
  void findOrdersOfCurrentUser_AsClient_ShouldReturnSortedDTOs() {
    Order olderOrder = new Order();
    olderOrder.setId(UUID.randomUUID());
    olderOrder.setUser(user);
    olderOrder.setOrderStatus(OrderStatus.PENDING);
    olderOrder.setOrderDate(LocalDateTime.now().minusDays(5)); // Más antiguo
    olderOrder.setItems(new ArrayList<>(List.of(item)));
    olderOrder.setOrderCode("CODE-OLD");
    olderOrder.setTotalPrice(100);

    when(authService.getCurrentUser()).thenReturn(user);
    when(storeRepository.findByUserId(user.getId())).thenReturn(Optional.empty());
    // Simulamos que la base de datos los devuelve desordenados (antiguo primero)
    when(orderRepository.findByUserId(user.getId())).thenReturn(List.of(olderOrder, order));

    List<OrderDTO> result = orderService.findOrdersOfCurrenUser();

    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
    // Verificamos que el más nuevo esté primero
    assertEquals(order.getId(), result.get(0).getId());
    assertEquals(olderOrder.getId(), result.get(1).getId());
    assertEquals("Tienda Test", result.get(0).getStoreName());
  }

  @Test
  void findOrdersOfCurrentUser_AsStore_ShouldReturnSortedStoreOrders() {
    Order olderOrder = new Order();
    olderOrder.setId(UUID.randomUUID());
    olderOrder.setUser(user);
    olderOrder.setOrderDate(LocalDateTime.now().minusDays(2));
    olderOrder.setItems(new ArrayList<>(List.of(item)));

    when(authService.getCurrentUser()).thenReturn(user);
    when(storeRepository.findByUserId(user.getId())).thenReturn(Optional.of(store));
    when(orderRepository.findByStoreId(store.getId())).thenReturn(List.of(olderOrder, order));

    List<OrderDTO> result = orderService.findOrdersOfCurrenUser();

    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
    // Verificamos el orden descendente
    assertEquals(order.getId(), result.get(0).getId());
    assertEquals(olderOrder.getId(), result.get(1).getId());
    verify(orderRepository).findByStoreId(store.getId());
  }

  @Test
  void findOrdersByUserId_ShouldReturnSortedList() {
    Order olderOrder = new Order();
    olderOrder.setId(UUID.randomUUID());
    olderOrder.setUser(user);
    olderOrder.setOrderDate(LocalDateTime.now().minusDays(10));
    olderOrder.setItems(new ArrayList<>(List.of(item)));

    when(orderRepository.findByUserId(user.getId())).thenReturn(List.of(olderOrder, order));

    List<OrderDTO> result = orderService.findOrdersByUserId(user.getId());

    assertEquals(2, result.size());
    assertEquals(order.getId(), result.get(0).getId());
    assertEquals(olderOrder.getId(), result.get(1).getId());
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
  void createOrder_ShouldCreateAndReturnDTO()
      throws ResourceNotFoundException, UnauthorizedException {
    when(authService.getCurrentUser()).thenReturn(user);
    when(productVariantService.getProductVariantById(variantId)).thenReturn(variant);
    when(orderRepository.save(any())).thenReturn(order);

    Map<UUID, Integer> variantIdsWithQuantity = new HashMap<>();
    variantIdsWithQuantity.put(variantId, 2);

    OrderDTO result = orderService.createOrder(variantIdsWithQuantity, null);

    assertNotNull(result);
    verify(orderRepository).save(any(Order.class));
  }

  @Test
  void createOrder_WithOutfitDiscount_ShouldApplyDiscount() throws Exception {
    UUID outfitId = UUID.randomUUID();
    Outfit outfit = new Outfit();
    outfit.setId(outfitId);
    outfit.setDiscountPercentage(20);

    when(authService.getCurrentUser()).thenReturn(user);
    when(productVariantService.getProductVariantById(variantId)).thenReturn(variant);
    when(outfitService.findById(outfitId)).thenReturn(outfit);
    when(orderRepository.save(any(Order.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Map<UUID, Integer> variantIdsWithQuantity = new HashMap<>();
    variantIdsWithQuantity.put(variantId, 2);

    OrderDTO result = orderService.createOrder(variantIdsWithQuantity, outfitId);

    assertNotNull(result);
    assertEquals(160, result.getTotalPrice());
    verify(orderRepository).save(any(Order.class));
  }

  @Test
  void createOrder_WithOutfitDiscount_ShouldTruncateCorrectly() throws Exception {
    UUID outfitId = UUID.randomUUID();
    Outfit outfit = new Outfit();
    outfit.setId(outfitId);
    outfit.setDiscountPercentage(25);

    UUID truncVariantId = UUID.randomUUID();
    Product productTruncateTest = new Product();
    productTruncateTest.setStore(store);
    productTruncateTest.setPriceInCents(3998);
    productTruncateTest.setName("Producto Truncado");

    ProductVariant truncVariant = new ProductVariant();
    truncVariant.setId(truncVariantId);
    truncVariant.setProduct(productTruncateTest);
    truncVariant.setSize(variant.getSize());
    truncVariant.setColor(variant.getColor());
    truncVariant.setIsAvailable(true);

    when(authService.getCurrentUser()).thenReturn(user);
    when(productVariantService.getProductVariantById(truncVariantId)).thenReturn(truncVariant);
    when(outfitService.findById(outfitId)).thenReturn(outfit);
    when(orderRepository.save(any(Order.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Map<UUID, Integer> variantIdsWithQuantity = new HashMap<>();
    variantIdsWithQuantity.put(truncVariantId, 1);

    OrderDTO result = orderService.createOrder(variantIdsWithQuantity, outfitId);

    assertNotNull(result);
    assertEquals(2998, result.getTotalPrice()); // 3998 * 0.75 = 2998.5 → truncated to 2998
  }

  @Test
  void confirmOrder_PendingStatus_ShouldWork() throws Exception {
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    when(stripeVerificationService.checkAccountIsVerifiedForPayments(any(Store.class)))
        .thenReturn(true);
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

  // Tests for variant-based createOrder
  @Test
  void createOrder_WithValidVariants_ShouldCreateOrderWithVariantDetails()
      throws ResourceNotFoundException, UnauthorizedException {
    when(authService.getCurrentUser()).thenReturn(user);
    when(productVariantService.getProductVariantById(variantId)).thenReturn(variant);
    when(orderRepository.save(any(Order.class))).thenReturn(order);

    Map<UUID, Integer> variantIdsWithQuantity = new HashMap<>();
    variantIdsWithQuantity.put(variantId, 2);

    OrderDTO result = orderService.createOrder(variantIdsWithQuantity, null);

    assertNotNull(result);
    assertEquals(user.getId(), result.getUserId());
    assertEquals(1, result.getItems().size());
    assertEquals(2, result.getItems().get(0).getQuantity());
    assertEquals(OrderStatus.PENDING, result.getOrderStatus());
  }

  @Test
  void createOrder_WithUnavailableVariant_ShouldThrowUnauthorizedException()
      throws ResourceNotFoundException {
    variant.setIsAvailable(false);
    when(authService.getCurrentUser()).thenReturn(user);
    when(productVariantService.getProductVariantById(variantId)).thenReturn(variant);

    Map<UUID, Integer> variantIdsWithQuantity = new HashMap<>();
    variantIdsWithQuantity.put(variantId, 2);

    assertThrows(
        UnauthorizedException.class, () -> orderService.createOrder(variantIdsWithQuantity, null));
  }

  @Test
  void createOrder_WithNonExistentVariant_ShouldThrowResourceNotFoundException()
      throws ResourceNotFoundException {
    when(authService.getCurrentUser()).thenReturn(user);
    when(productVariantService.getProductVariantById(variantId))
        .thenThrow(new ResourceNotFoundException("Variant not found"));

    Map<UUID, Integer> variantIdsWithQuantity = new HashMap<>();
    variantIdsWithQuantity.put(variantId, 2);

    assertThrows(
        ResourceNotFoundException.class,
        () -> orderService.createOrder(variantIdsWithQuantity, null));
  }

  @Test
  void createOrder_WithMultipleVariants_ShouldCreateOrderWithAllVariants()
      throws ResourceNotFoundException, UnauthorizedException {
    UUID variantId2 = UUID.randomUUID();
    Product product2 = new Product();
    product2.setId(UUID.randomUUID());
    product2.setName("Test Product 2");
    product2.setPriceInCents(200);
    product2.setType(new ProductType());
    product2.setStore(store);

    ProductVariant variant2 = new ProductVariant();
    variant2.setId(variantId2);
    variant2.setProduct(product2);
    variant2.setSize(variant.getSize());
    variant2.setColor(variant.getColor());
    variant2.setIsAvailable(true);

    when(authService.getCurrentUser()).thenReturn(user);
    when(productVariantService.getProductVariantById(variantId)).thenReturn(variant);
    when(productVariantService.getProductVariantById(variantId2)).thenReturn(variant2);
    when(orderRepository.save(any(Order.class))).thenReturn(order);

    Map<UUID, Integer> variantIdsWithQuantity = new HashMap<>();
    variantIdsWithQuantity.put(variantId, 1);
    variantIdsWithQuantity.put(variantId2, 2);

    OrderDTO result = orderService.createOrder(variantIdsWithQuantity, null);

    assertNotNull(result);
    assertEquals(OrderStatus.PENDING, result.getOrderStatus());
  }

  @Test
  void createOrder_CalculatesTotalPriceCorrectly()
      throws ResourceNotFoundException, UnauthorizedException {
    when(authService.getCurrentUser()).thenReturn(user);
    when(productVariantService.getProductVariantById(variantId)).thenReturn(variant);
    when(orderRepository.save(any(Order.class)))
        .thenAnswer(
            invocation -> {
              Order savedOrder = invocation.getArgument(0);
              // Verify total price was calculated correctly (100 price * 2 quantity = 200)
              assertEquals(200, savedOrder.getTotalPrice());
              return savedOrder;
            });

    Map<UUID, Integer> variantIdsWithQuantity = new HashMap<>();
    variantIdsWithQuantity.put(variantId, 2);

    OrderDTO result = orderService.createOrder(variantIdsWithQuantity, null);

    assertEquals(200, result.getTotalPrice());
  }
}
