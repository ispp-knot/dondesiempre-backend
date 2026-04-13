package ispp.project.dondesiempre.seed;

import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.clients.models.Client;
import ispp.project.dondesiempre.modules.follows.models.StoreFollower;
import ispp.project.dondesiempre.modules.orders.models.Order;
import ispp.project.dondesiempre.modules.orders.models.OrderItem;
import ispp.project.dondesiempre.modules.orders.models.OrderStatus;
import ispp.project.dondesiempre.modules.products.models.Product;
import ispp.project.dondesiempre.modules.stores.models.Store;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

class ClientSeed {

  private final DataSeeder s;

  ClientSeed(DataSeeder seeder) {
    this.s = seeder;
  }

  void seed() {
    Random rng = new Random(s.props.getRandomSeed());

    List<String> firstNames = s.loadTextFile("seed/client-first-names.txt");
    List<String> surnames = s.loadTextFile("seed/client-surnames.txt");

    List<Store> allStores = s.storeRepository.findAll();
    List<Product> allProducts = s.productRepository.findAll();

    User clientUser = new User();
    clientUser.setId(s.seedUuid("user:client@client.com"));
    clientUser.setEmail("client@client.com");
    clientUser.setPassword(s.passwordEncoder.encode("Password123!"));
    s.userRepository.save(clientUser);

    Client manualClient = new Client();
    manualClient.setId(s.seedUuid("client:client@client.com"));
    manualClient.setName("Ana");
    manualClient.setSurname("García");
    manualClient.setUser(clientUser);
    s.clientRepository.save(manualClient);

    Product p1 = allProducts.size() > 0 ? allProducts.get(0) : null;
    Product p3 = allProducts.size() > 2 ? allProducts.get(2) : p1;
    Product p4 = allProducts.size() > 3 ? allProducts.get(3) : p1;

    if (p3 != null && p4 != null && p1 != null) {
      Order order = new Order();
      order.setUser(clientUser);
      order.setOrderDate(LocalDateTime.now());
      order.setOrderStatus(OrderStatus.PENDING);
      order.setOrderCode("SEED-MANU-0001");
      order.setItems(new ArrayList<>());
      s.addItemsToOrder(order, List.of(p3));

      Order orderConfirmed = new Order();
      orderConfirmed.setUser(clientUser);
      orderConfirmed.setOrderDate(LocalDateTime.now().minusDays(2));
      orderConfirmed.setOrderStatus(OrderStatus.CONFIRMED);
      orderConfirmed.setOrderCode("SEED-CONF-0002");
      orderConfirmed.setItems(new ArrayList<>());
      s.addItemsToOrder(orderConfirmed, List.of(p3));
      s.orderRepository.save(orderConfirmed);

      Order orderRejected = new Order();
      orderRejected.setUser(clientUser);
      orderRejected.setOrderDate(LocalDateTime.now().minusDays(5));
      orderRejected.setOrderStatus(OrderStatus.REJECTED);
      orderRejected.setOrderCode("SEED-REJE-0003");
      orderRejected.setItems(new ArrayList<>());
      s.addItemsToOrder(orderRejected, List.of(p4));
      s.orderRepository.save(orderRejected);

      Order orderPicked = new Order();
      orderPicked.setUser(clientUser);
      orderPicked.setOrderDate(LocalDateTime.now().minusDays(1));
      orderPicked.setOrderStatus(OrderStatus.PICKED);
      orderPicked.setOrderCode("SEED-PICK-0004");
      orderPicked.setItems(new ArrayList<>());
      s.addItemsToOrder(orderPicked, List.of(p1, p4));
      s.orderRepository.save(orderPicked);
    }

    for (int i = 1; i <= s.props.getClientCount(); i++) {
      String name = s.pick(firstNames, rng);
      String surname = s.pick(surnames, rng);
      String clientEmail = "client" + i + "@client.com";

      User user = new User();
      user.setId(s.seedUuid("user:" + clientEmail));
      user.setEmail(clientEmail);
      user.setPassword(s.passwordEncoder.encode("Password123!"));
      s.userRepository.save(user);

      Client client = new Client();
      client.setId(s.seedUuid("client:" + clientEmail));
      client.setName(name);
      client.setSurname(surname);
      client.setUser(user);
      s.clientRepository.save(client);

      if (rng.nextDouble() < 0.5 && !allProducts.isEmpty()) {
        Order randomOrder = new Order();
        randomOrder.setUser(user);
        randomOrder.setOrderDate(LocalDateTime.now().minusDays(rng.nextInt(10)));
        randomOrder.setOrderStatus(OrderStatus.PENDING);
        String uuidStr = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        randomOrder.setOrderCode("SEED-" + uuidStr.substring(0, 4) + "-" + uuidStr.substring(4, 8));
        randomOrder.setItems(new ArrayList<>());

        int itemsToCreate = 1 + rng.nextInt(3);
        int randomTotal = 0;

        for (int k = 0; k < itemsToCreate; k++) {
          Product p = s.pick(allProducts, rng);
          OrderItem item = new OrderItem();
          item.setOrder(randomOrder);
          item.setProduct(p);
          item.setQuantity(1 + rng.nextInt(2));
          item.setPriceAtPurchase(p.getPriceInCents());
          randomOrder.getItems().add(item);
          randomTotal += item.getPriceAtPurchase() * item.getQuantity();
        }
        randomOrder.setTotalPrice(randomTotal);
        s.orderRepository.save(randomOrder);
      }

      int followCount = 1 + rng.nextInt(3);
      List<Store> shuffled = new ArrayList<>(allStores);
      for (int j = 0; j < followCount && j < shuffled.size(); j++) {
        int idx = j + rng.nextInt(shuffled.size() - j);
        Store store = shuffled.get(idx);
        shuffled.set(idx, shuffled.get(j));
        shuffled.set(j, store);

        StoreFollower follower = new StoreFollower();
        follower.setClient(client);
        follower.setStore(store);
        s.storeFollowerRepository.save(follower);
      }
    }
  }
}
