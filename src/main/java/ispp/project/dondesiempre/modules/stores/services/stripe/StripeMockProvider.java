package ispp.project.dondesiempre.modules.stores.services.stripe;

import ispp.project.dondesiempre.modules.stores.models.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("test") // ¡Solo se activa durante los tests!
public class StripeMockProvider implements StripeProvider {
  private static final Logger logger = LoggerFactory.getLogger(StripeMockProvider.class);

  @Override
  public String createConnectAccount(Store store) {
    logger.info("MOCK: Simulando creación de cuenta en Stripe para: {}", store.getEmail());
    // Devolvemos un ID falso con formato similar al de Stripe
    return "acct_mock_" + System.currentTimeMillis();
  }
}
