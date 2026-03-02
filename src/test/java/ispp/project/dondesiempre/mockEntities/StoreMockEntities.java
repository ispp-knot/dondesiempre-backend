package ispp.project.dondesiempre.mockEntities;

import ispp.project.dondesiempre.models.Client;
import ispp.project.dondesiempre.models.User;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.models.stores.StoreFollower;
import ispp.project.dondesiempre.services.UserService;
import java.util.UUID;

public class StoreMockEntities {

  public static Store sampleStore(UUID TEST_STORE_ID) {
    Store store = new Store();
    store.setId(TEST_STORE_ID);
    store.setName("Tienda de Prueba");

    return store;
  }

  public static Client sampleClient() {
    User user = new User();
    user.setId(UUID.randomUUID());
    user.setEmail(UserService.SEED_USER_EMAIL);

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
