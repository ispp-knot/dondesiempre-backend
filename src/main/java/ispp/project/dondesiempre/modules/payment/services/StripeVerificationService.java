package ispp.project.dondesiempre.modules.payment.services;

import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.Account.Requirements;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.common.exceptions.StripeFailException;
import ispp.project.dondesiempre.modules.stores.models.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripeVerificationService {

  public boolean checkAccountIsVerifiedForPayments(Store store) {

    if (store == null) throw new ResourceNotFoundException();

    boolean verified = false;
    String accountId =
        store
            .getAccountId()
            .orElseThrow(
                () ->
                    new StripeFailException(
                        "La tienda no tiene cuenta de stripe asociada, contacte con soporte"));

    try {
      Account account = Account.retrieve(accountId);
      Requirements requirements = account.getRequirements();
      verified =
          account.getPayoutsEnabled()
              && account.getChargesEnabled()
              && requirements != null
              && requirements.getCurrentlyDue().isEmpty();
    } catch (StripeException e) {
      throw new StripeFailException(e.getMessage());
    }

    return verified;
  }
}
