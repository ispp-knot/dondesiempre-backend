package ispp.project.dondesiempre.modules.order.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ispp.project.dondesiempre.config.coordinates.GeometryFactoryConfig;
import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.orders.models.Order;
import ispp.project.dondesiempre.modules.orders.models.OrderItem;
import ispp.project.dondesiempre.modules.orders.models.OrderStatus;
import ispp.project.dondesiempre.modules.orders.repositories.OrderRepository;
import ispp.project.dondesiempre.modules.products.models.Product;
import ispp.project.dondesiempre.modules.products.models.ProductType;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.models.Storefront;
import ispp.project.dondesiempre.utils.cloudinary.CoordinatesService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({CoordinatesService.class, GeometryFactoryConfig.class})
public class OrderRepositoryTest {

  @Autowired private OrderRepository orderRepository;
  @Autowired private TestEntityManager entityManager;
  @Autowired private CoordinatesService coordinatesService;

  private User savedUser;
  private Store savedStore;
  private String testOrderCode = "TEST-ORDER-CODE-123";
  private final String PAYMENT_INTENT_ID = "pi_asuhuiashuidahiuahaskoa";

  @BeforeEach
  void setUp() {
    savedUser = new User();
    savedUser.setEmail("order-repo-test-" + UUID.randomUUID() + "@test.com");
    savedUser.setPassword("password");
    entityManager.persist(savedUser);

    Storefront storefront = new Storefront();
    entityManager.persist(storefront);

    savedStore = new Store();
    savedStore.setName("Order Test Store");
    savedStore.setEmail("order-store-" + UUID.randomUUID() + "@test.com");
    savedStore.setAddress("Test address");
    savedStore.setOpeningHours("9-5");
    savedStore.setAcceptsShipping(false);
    savedStore.setLocation(coordinatesService.createPoint(0.0, 0.0));
    savedStore.setStorefront(storefront);
    savedStore.setUser(savedUser);
    entityManager.persist(savedStore);

    ProductType type = new ProductType();
    type.setType("Order Test Type");
    entityManager.persist(type);

    Product product = new Product();
    product.setName("Test Product");
    product.setPriceInCents(500);
    product.setType(type);
    product.setStore(savedStore);
    entityManager.persist(product);

    Order order = new Order();
    order.setUser(savedUser);
    order.setOrderCode(testOrderCode);
    order.setOrderDate(LocalDateTime.now());
    order.setOrderStatus(OrderStatus.PENDING);
    order.setTotalPrice(500);
    order.setItems(new ArrayList<>());

    OrderItem item = new OrderItem();
    item.setProduct(product);
    item.setQuantity(1);
    item.setPriceAtPurchase(500);
    item.setOrder(order);
    order.getItems().add(item);

    entityManager.persist(order);
    entityManager.flush();
    entityManager.clear();
  }

  @Test
  void shouldFindByUserId() {
    List<Order> orders = orderRepository.findByUserId(savedUser.getId());
    assertNotNull(orders);
    assertFalse(orders.isEmpty());
  }

  @Test
  void shouldFindByStoreId() {
    List<Order> orders = orderRepository.findByStoreId(savedStore.getId());
    assertNotNull(orders);
    assertFalse(orders.isEmpty());
    assertEquals(
        savedStore.getId(), orders.get(0).getItems().get(0).getProduct().getStore().getId());
  }

  @Test
  void shouldFindByOrderCode() {
    Optional<Order> order = orderRepository.findByOrderCode(testOrderCode);
    assertTrue(order.isPresent());
    assertEquals(testOrderCode, order.get().getOrderCode());
  }

  @Test
  void shouldNotFindNonExistentOrderCode() {
    Optional<Order> order = orderRepository.findByOrderCode("NON-EXISTENT-CODE");
    assertFalse(order.isPresent());
  }

  @Test
  void shouldReturnFalse_whenPaymentIntentIdIsNull() {
    Order order = orderRepository.findByOrderCode(testOrderCode).get();
    boolean res = orderRepository.existsByIdAndPaymentIntentIdIsNotNull(order.getId());
    assertFalse(res);
  }

  @Test
  void shouldReturnTrue_whenPaymentIntentIdIsNotNull() {
    Order order = orderRepository.findByOrderCode(testOrderCode).get();
    order.setPaymentIntentId(PAYMENT_INTENT_ID);
    orderRepository.save(order);
    boolean res = orderRepository.existsByIdAndPaymentIntentIdIsNotNull(order.getId());
    assertTrue(res);
  }

  @Test
  void shouldReturnOrder_whenFindingByPaymentIntentId() {
    Order order = orderRepository.findByOrderCode(testOrderCode).get();
    order.setPaymentIntentId(PAYMENT_INTENT_ID);
    orderRepository.save(order);
    Optional<Order> res = orderRepository.findByPaymentIntentId(PAYMENT_INTENT_ID);
    assertTrue(res.isPresent());
  }
}
