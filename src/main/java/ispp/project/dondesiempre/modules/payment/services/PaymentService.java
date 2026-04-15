package ispp.project.dondesiempre.modules.payment.services;

import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.StripeException;
import com.stripe.model.AccountLink;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.LoginLink;
import com.stripe.model.Refund;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.param.AccountLinkCreateParams;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.auth.services.AuthService;
import ispp.project.dondesiempre.modules.common.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.modules.common.exceptions.StoreNotVerifiedException;
import ispp.project.dondesiempre.modules.common.exceptions.StripeFailException;
import ispp.project.dondesiempre.modules.common.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.modules.orders.models.Order;
import ispp.project.dondesiempre.modules.orders.models.OrderItem;
import ispp.project.dondesiempre.modules.orders.services.OrderService;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.services.StoreService;
import ispp.project.dondesiempre.modules.stores.services.stripe.StripeProvider;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

  private final OrderService orderService;
  private final StoreService storeService;
  private final AuthService authService;
  private final StripeProvider stripeProvider;
  private final ApplicationContext applicationContext;
  private final StripeVerificationService stripeVerificationService;

  @Value("${frontend.url}")
  private String frontendUrl;

  private float freeFeePercentage = 0.05f;
  private float premiumFeePercentage = 0.02f;

  @Transactional
  public void capturePayment(Event event) throws StripeException {
    if (!"checkout.session.completed".equals(event.getType())) {
      return;
    }

    Session session = this.deserializeSession(event);
    if (session == null) {
      return;
    }

    String rawOrderId = session.getMetadata() != null ? session.getMetadata().get("orderId") : null;

    if (rawOrderId == null || rawOrderId.isBlank()) {
      return;
    }

    String paymentIntentId = session.getPaymentIntent();

    if (paymentIntentId == null || paymentIntentId.isBlank()) {
      return;
    }

    UUID orderId = UUID.fromString(rawOrderId);

    try {
      orderService.findById(orderId);
    } catch (Throwable _) {
      this.refundPaymentIntent(paymentIntentId);
      return;
    }

    applicationContext.getBean(PaymentService.class).registerPaymentOrder(orderId, paymentIntentId);
  }

  private Session deserializeSession(Event event) {
    EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();

    StripeObject obj =
        deserializer
            .getObject()
            .orElseGet(
                () -> {
                  try {
                    return deserializer.deserializeUnsafe();
                  } catch (EventDataObjectDeserializationException e) {
                    return null;
                  }
                });

    return obj instanceof Session s ? s : null;
  }

  @Transactional
  public Order registerPaymentOrder(UUID orderId, String paymentIntentId) {

    Order order = orderService.setPaymentIntentId(orderId, paymentIntentId);

    return order;
  }

  @Transactional
  public String initiateOrderPayment(UUID orderId) {
    Order order = orderService.findById(orderId);

    User user = authService.getCurrentUser();
    if (!user.equals(order.getUser())) {
      throw new UnauthorizedException(
          "Order Does Not Belong To This User With Id: " + user.getId());
    }
    if (order.getPaymentIntentId().isPresent())
      throw new InvalidRequestException("The order is already paid");

    boolean isVerified =
        stripeVerificationService.checkAccountIsVerifiedForPayments(
            order
                .getStore()
                .orElseThrow(() -> new InvalidRequestException("El pedido no tiene productos.")));

    if (!isVerified) throw new StoreNotVerifiedException();

    String redirectUrl = String.format("%s/orders/checkout/%s", frontendUrl, orderId.toString());
    String cancelUrl = String.format("%s/orders", frontendUrl);
    String sessionUrl = this.getCheckoutSession(order, redirectUrl, cancelUrl);
    return sessionUrl;
  }

  private String getCheckoutSession(Order order, String successUrl, String cancelUrl) {
    User user = authService.getCurrentUser();

    long totalAmount = order.getTotalPrice();

    Store store =
        order
            .getStore()
            .orElseThrow(() -> new InvalidRequestException("El pedido no tenía productos."));

    float fee =
        applicationContext.getBean(PaymentService.class).checkIfStoreIsPremium(store.getId())
            ? premiumFeePercentage
            : freeFeePercentage;
    long applicationFee = Math.round(totalAmount * fee);
    String accountId = store.getAccountId().get();
    try {
      SessionCreateParams sessionParams =
          SessionCreateParams.builder()
              .setMode(SessionCreateParams.Mode.PAYMENT)
              .setBillingAddressCollection(SessionCreateParams.BillingAddressCollection.REQUIRED)
              .setSuccessUrl(successUrl)
              .setCancelUrl(cancelUrl)
              .setCurrency("eur")
              .setPaymentIntentData(
                  SessionCreateParams.PaymentIntentData.builder()
                      .setApplicationFeeAmount(applicationFee)
                      .setTransferData(
                          SessionCreateParams.PaymentIntentData.TransferData.builder()
                              .setDestination(accountId)
                              .build())
                      .build())
              .putMetadata("orderId", order.getId().toString())
              .putMetadata("userId", user.getId().toString())
              .addAllLineItem(createLineItemsFromOrder(order))
              .build();
      Session session = Session.create(sessionParams);
      return session.getUrl();
    } catch (StripeException e) {
      throw new StripeFailException(e.getMessage());
    }
  }

  @Transactional(readOnly = true)
  public boolean checkIfStoreIsPremium(UUID storeId) {

    return storeService.checkStoreIsPremium(storeId);
  }

  private List<SessionCreateParams.LineItem> createLineItemsFromOrder(Order order) {

    Function<OrderItem, SessionCreateParams.LineItem> orderToLineItem =
        (orderItem) -> {
          SessionCreateParams.LineItem item =
              SessionCreateParams.LineItem.builder()
                  .setQuantity(orderItem.getQuantity().longValue())
                  .setPriceData(
                      SessionCreateParams.LineItem.PriceData.builder()
                          .setUnitAmount(orderItem.getPriceAtPurchase().longValue())
                          .setCurrency("eur")
                          .setProductData(
                              SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                  .setName(orderItem.getProduct().getName())
                                  .build())
                          .build())
                  .build();
          return item;
        };

    return order.getItems().stream().map(orderToLineItem::apply).toList();
  }

  public Refund refundPaymentIntent(String paymentIntentId) throws StripeException {
    RefundCreateParams redunParams =
        RefundCreateParams.builder()
            .setPaymentIntent(paymentIntentId)
            .setReverseTransfer(true)
            .build();
    return Refund.create(redunParams);
  }

  @Transactional
  public void createConnectAccount(Store store) {
    String accountId = stripeProvider.createConnectAccount(store);

    storeService.setAccountId(store.getId(), accountId);
  }

  public String getStripeOnboardingLink(Store store) {
    authService.assertUserOwnsStore(store);
    String stripeAccountId =
        store
            .getAccountId()
            .orElseThrow(
                () ->
                    new StripeFailException(
                        "La tienda no tiene cuenta de stripe asociada, contacte con soporte"));
    String profileUrl = frontendUrl + "/" + "/profile";
    AccountLinkCreateParams params =
        AccountLinkCreateParams.builder()
            .setAccount(stripeAccountId)
            .setRefreshUrl(profileUrl)
            .setReturnUrl(profileUrl)
            .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
            .build();
    AccountLink accountLink;
    try {
      accountLink = AccountLink.create(params);

    } catch (Exception e) {
      throw new StripeFailException("Hubo un fallo al generar el link de verificación.");
    }
    return accountLink.getUrl();
  }

  public String getStripeDashboardLink(Store store) {

    authService.assertUserOwnsStore(store);

    String stripeAccountId =
        store
            .getAccountId()
            .orElseThrow(
                () ->
                    new StripeFailException(
                        "La tienda no tiene cuenta de stripe asociada, contacte con soporte"));
    LoginLink loginLink;
    try {
      loginLink = LoginLink.createOnAccount(stripeAccountId);
    } catch (StripeException e) {
      throw new StripeFailException("Hubo un fallo al generar el link de de acceso al dashboard.");
    }

    return loginLink.getUrl();
  }
}
