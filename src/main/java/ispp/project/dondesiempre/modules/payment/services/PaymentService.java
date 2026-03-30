package ispp.project.dondesiempre.modules.payment.services;

import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.param.AccountCreateParams;
import ispp.project.dondesiempre.modules.auth.services.AuthService;
import ispp.project.dondesiempre.modules.common.exceptions.StripeFailException;
import ispp.project.dondesiempre.modules.orders.services.OrderService;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.services.StoreService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

  OrderService orderService;
  StoreService storeService;
  AuthService authService;
  private final String frontendUrl;
  private final float freeFeePercentage = 0.05f;
  private final float premiumFeePercentage = 0.02f;

  public PaymentService(
      OrderService orderService,
      @Value("${frontend.url}") String frontendUrl,
      AuthService authService,
      StoreService storeService) {
    this.orderService = orderService;
    this.frontendUrl = frontendUrl;
    this.authService = authService;
    this.storeService = storeService;
  }

  @Transactional
  public void createConnectAccount(Store store) {

    AccountCreateParams params =
        AccountCreateParams.builder()
            .setType(AccountCreateParams.Type.EXPRESS)
            .setEmail(store.getEmail())
            .setBusinessType(AccountCreateParams.BusinessType.COMPANY)
            .setCapabilities(
                AccountCreateParams.Capabilities.builder()
                    .setCardPayments(
                        AccountCreateParams.Capabilities.CardPayments.builder()
                            .setRequested(true)
                            .build())
                    .setTransfers(
                        AccountCreateParams.Capabilities.Transfers.builder()
                            .setRequested(true)
                            .build())
                    .build())
            .putMetadata("storeName", store.getName())
            .build();

    Account account;
    try {
      account = Account.create(params);
    } catch (StripeException e) {
      throw new StripeFailException("Fail when creating stripe account");
    }
    storeService.setAccountId(store.getId(), account.getId());
  }
}
