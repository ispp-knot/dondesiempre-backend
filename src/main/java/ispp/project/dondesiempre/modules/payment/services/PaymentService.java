package ispp.project.dondesiempre.modules.payment.services;

import ispp.project.dondesiempre.modules.auth.services.AuthService;
import ispp.project.dondesiempre.modules.orders.services.OrderService;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.services.StoreService;
import ispp.project.dondesiempre.modules.stores.services.stripe.StripeProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

  OrderService orderService;
  StoreService storeService;
  AuthService authService;
  StripeProvider stripeProvider;
  private final String frontendUrl;
  private final float freeFeePercentage = 0.05f;
  private final float premiumFeePercentage = 0.02f;

  public PaymentService(
      OrderService orderService,
      @Value("${frontend.url}") String frontendUrl,
      AuthService authService,
      StoreService storeService,
      StripeProvider stripeProvider) {
    this.orderService = orderService;
    this.frontendUrl = frontendUrl;
    this.authService = authService;
    this.storeService = storeService;
    this.stripeProvider = stripeProvider;
  }

  @Transactional
  public void createConnectAccount(Store store) {
    // Ya no hay lógica de Stripe aquí, solo llamamos al proveedor
    String accountId = stripeProvider.createConnectAccount(store);

    // Guardamos el ID (ya sea el real o el mock)
    storeService.setAccountId(store.getId(), accountId);
  }
}
