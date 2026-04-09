package ispp.project.dondesiempre.modules.stores.services.stripe;

import ispp.project.dondesiempre.modules.stores.models.Store;

public interface StripeProvider {
  String createConnectAccount(Store store);
}
