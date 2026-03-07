package ispp.project.dondesiempre.mockEntities;

import ispp.project.dondesiempre.models.storefronts.Storefront;
import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.clients.models.Client;
import ispp.project.dondesiempre.modules.follows.models.StoreFollower;
import ispp.project.dondesiempre.modules.stores.models.Store;

import java.util.UUID;

public class StoreMockEntities {

  public static Store sampleStore(UUID TEST_STORE_ID) {
    Store store = new Store();
    store.setId(TEST_STORE_ID);
    store.setName("Tienda de Prueba");
    Storefront storefront = new Storefront();
    storefront.setIsFirstCollections(true);
    store.setStorefront(storefront);
    return store;
  }

  public static Client sampleClient() {
    User user = new User();
    user.setId(UUID.randomUUID());
    user.setEmail("client@client.com");

    Client client = new Client();
    client.setId(UUID.randomUUID());
    client.setUser(user);

    return client;
  }

  public static StoreFollower sampleFollower(Client TEST_CLIENT, Store TEST_STORE) {
    StoreFollower follower = new StoreFollower();
    follower.setClient(TEST_CLIENT);
    follower.setStore(TEST_STORE);

    return follower;
  }
}
