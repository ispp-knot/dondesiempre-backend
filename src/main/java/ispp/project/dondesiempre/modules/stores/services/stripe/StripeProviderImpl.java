package ispp.project.dondesiempre.modules.stores.services.stripe;

import com.stripe.model.Account;
import com.stripe.param.AccountCreateParams;
import ispp.project.dondesiempre.modules.common.exceptions.StripeFailException;
import ispp.project.dondesiempre.modules.stores.models.Store;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!test") // Se activa en dev, prod, etc.
public class StripeProviderImpl implements StripeProvider {

  @Override
  public String createConnectAccount(Store store) {
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

    try {
      return Account.create(params).getId();
    } catch (Exception e) {
      throw new StripeFailException("Error real de Stripe: " + e.getMessage());
    }
  }
}
