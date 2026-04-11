package ispp.project.dondesiempre.modules.orders.services;

import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.auth.repositories.UserRepository;
import ispp.project.dondesiempre.modules.auth.services.AuthService;
import ispp.project.dondesiempre.modules.common.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.common.exceptions.StoreNotVerifiedException;
import ispp.project.dondesiempre.modules.common.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.modules.orders.dtos.OrderDTO;
import ispp.project.dondesiempre.modules.orders.models.Order;
import ispp.project.dondesiempre.modules.orders.models.OrderItem;
import ispp.project.dondesiempre.modules.orders.models.OrderStatus;
import ispp.project.dondesiempre.modules.orders.repositories.OrderRepository;
import ispp.project.dondesiempre.modules.outfits.models.Outfit;
import ispp.project.dondesiempre.modules.outfits.services.OutfitService;
import ispp.project.dondesiempre.modules.payment.services.StripeVerificationService;
import ispp.project.dondesiempre.modules.products.models.Product;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.repositories.StoreRepository;
import ispp.project.dondesiempre.utils.crypto.CryptoConverter;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final UserRepository userRepository;
  private final StoreRepository storeRepository;
  private final AuthService authService;
  private final CryptoConverter cryptoConverter;
  private final ApplicationContext applicationContext;
  private final OutfitService outfitService;
  private final StripeVerificationService stripeVerificationService;

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

    return orders.stream().map(OrderDTO::new).toList();
  }

  @Transactional(readOnly = true)
  public List<OrderDTO> findOrdersByUserId(UUID userId) {
    return orderRepository.findByUserId(userId).stream().map(OrderDTO::new).toList();
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

  @Transactional
  public Order setPaymentIntentId(UUID orderId, String paymentIntentId) {
    Order order = applicationContext.getBean(OrderService.class).findById(orderId);

    order.setPaymentIntentId(paymentIntentId);
    return order;
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

  @Transactional(rollbackFor = ResourceNotFoundException.class)
  public OrderDTO createOrder(Map<Product, Integer> productsToBuy, UUID outfitId)
      throws ResourceNotFoundException {
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

    Integer total = this.calculateAndSetTotalPrice(order);

    if (outfitId != null) {
      Outfit outfit = outfitService.findById(outfitId);
      if (outfit != null && outfit.getDiscountPercentage().isPresent()) {
        Integer discount = outfit.getDiscountPercentage().get();
        total = (total * (100 - discount)) / 100;
      }
    }

    order.setTotalPrice(total);
    Order savedOrder = orderRepository.save(order);

    return new OrderDTO(savedOrder);
  }

  @Transactional
  public void confirmOrder(UUID orderId) throws UnauthorizedException, ResourceNotFoundException {
    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Order with ID" + orderId + "not found"));
    if (order.getOrderStatus().equals(OrderStatus.PENDING)) {
      boolean verified =
          stripeVerificationService.checkAccountIsVerifiedForPayments(
              order
                  .getStore()
                  .orElseThrow(
                      () ->
                          new InvalidRequestException("There cannot be orders without products.")));

      if (!verified) throw new StoreNotVerifiedException();

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
    authService.assertUserOwnsStore(order.getItems().getFirst().getProduct().getStore());
    if (order.getOrderStatus().equals(OrderStatus.PENDING)) {
      order.setOrderStatus(OrderStatus.REJECTED);
    } else {
      throw new UnauthorizedException(
          "This order is not in the pending state, it cannot be rejected.");
    }
  }

  @Transactional(readOnly = true)
  public OrderDTO findOrder(String orderCode)
      throws UnauthorizedException, ResourceNotFoundException {
    String encryptedCode = cryptoConverter.convertToDatabaseColumn(orderCode);
    Order order =
        orderRepository
            .findByOrderCode(encryptedCode)
            .orElseThrow(
                () -> new ResourceNotFoundException("Order with Code" + orderCode + "not found"));
    authService.assertUserOwnsStore(order.getItems().getFirst().getProduct().getStore());
    return new OrderDTO(order);
  }

  @Transactional
  public void pickOrder(UUID orderId) throws UnauthorizedException, ResourceNotFoundException {
    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Order with ID" + orderId + "not found"));
    authService.assertUserOwnsStore(order.getItems().getFirst().getProduct().getStore());
    if (order.getOrderStatus().equals(OrderStatus.CONFIRMED)) {
      order.setOrderStatus(OrderStatus.PICKED);
    } else {
      throw new UnauthorizedException(
          "This order is not in the CONFIRMED state, it cannot be in the picked state.");
    }
  }

  @Transactional
  public void cancelOrder(UUID orderId) throws UnauthorizedException, ResourceNotFoundException {
    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Order with ID" + orderId + "not found"));
    authService.assertUserOwnsStore(order.getItems().getFirst().getProduct().getStore());
    if (order.getOrderStatus().equals(OrderStatus.PENDING)) {
      order.setOrderStatus(OrderStatus.CANCELLED);
    } else {
      throw new UnauthorizedException(
          "This order is not in the pending state, it cannot be canceled.");
    }
  }

  @Transactional(readOnly = true)
  public boolean irOrderPaid(UUID orderId) {
    return orderRepository.existsByIdAndPaymentIntentIdIsNotNull(orderId);
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
