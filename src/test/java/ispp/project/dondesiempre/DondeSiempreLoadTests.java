package ispp.project.dondesiempre;

import static org.assertj.core.api.Assertions.assertThat;

import ispp.project.dondesiempre.modules.auth.dtos.LoginRequestDTO;
import ispp.project.dondesiempre.modules.auth.dtos.LoginResponseDTO;
import ispp.project.dondesiempre.modules.auth.repositories.UserRepository;
import ispp.project.dondesiempre.modules.orders.dtos.OrderDTO;
import ispp.project.dondesiempre.modules.orders.models.OrderStatus;
import ispp.project.dondesiempre.modules.products.dtos.ProductDTO;
import ispp.project.dondesiempre.modules.stores.dtos.StoreDTO;
import ispp.project.dondesiempre.modules.stores.dtos.StorefrontDTO;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.repositories.StoreRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

// We're running the full server for this
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Tag("load")
public class DondeSiempreLoadTests {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private UserRepository userRepository;
  @Autowired private StoreRepository storeRepository;

  @Test
  void shouldTakeLessThan5SecondsOnAverageAnd10Max_when10UsersAnd5OwnersUseTheApp()
      throws InterruptedException, ExecutionException {
    // * Test that the database is seeded
    // * Get data for the stores
    // * Create tasks for clients and stores
    // * Execute tasks for clients and stores
    // * Check the timings

    // Test that the database is seeded
    assertThat(storeRepository.count())
        .withFailMessage("Test database must be seeded for the load tests to be run.")
        .isGreaterThan(0);

    // Get data for the stores
    List<String> stores =
        List.of(
            "demo@gretacloset.com",
            "demo@margovantes.com",
            "demo@pineapplemoda.com",
            "demo@roire.com",
            "demo@romantikavintage.com",
            "demo@confeccionesyhogarsansebastian.com");

    List<Store> storeData =
        stores.stream()
            .map(email -> userRepository.findByEmail(email).get())
            .map(user -> storeRepository.findByUserId(user.getId()).get())
            .toList();

    // Create tasks
    Stream<Callable<Long>> clientTasks =
        IntStream.range(0, 10)
            .mapToObj(
                i ->
                    (Callable<Long>)
                        () -> {
                          long start = System.currentTimeMillis();

                          String email = "client" + (i + 1) + "@client.com";
                          executeClientOperations(email, storeData.get(i % 5));

                          return System.currentTimeMillis() - start;
                        });

    Stream<Callable<Long>> storeTasks =
        IntStream.range(0, 5)
            .mapToObj(
                i ->
                    (Callable<Long>)
                        () -> {
                          long start = System.currentTimeMillis();

                          executeStoreOperations(stores.get(i), storeData.get(i));

                          return System.currentTimeMillis() - start;
                        });

    List<Callable<Long>> tasks = Stream.concat(clientTasks, storeTasks).toList();

    // Execute tasks
    ExecutorService executor = Executors.newFixedThreadPool(15);

    List<Future<Long>> results = executor.invokeAll(tasks);

    // Calculate times
    List<Long> times = new ArrayList<>();

    for (Future<Long> future : results) {
      times.add(future.get());
    }

    double avg = times.stream().mapToLong(Long::longValue).average().orElse(100000);
    long max = times.stream().mapToLong(Long::longValue).max().orElse(100000);

    System.out.println("Average time: " + avg + "ms, Max time: " + max + "ms");

    assertThat(avg).isLessThan(5000);
    assertThat(max).isLessThan(10000);

    executor.shutdown();
    executor.awaitTermination(1, TimeUnit.MINUTES);
  }

  String logInAndGetToken(String email) {
    LoginRequestDTO dto = new LoginRequestDTO(email, "Password123!");

    ResponseEntity<LoginResponseDTO> response =
        restTemplate.postForEntity("/api/v1/auth/login", dto, LoginResponseDTO.class);

    assertThat(response.getStatusCode().is2xxSuccessful())
        .withFailMessage("Login failed for " + email)
        .isTrue();

    return response.getBody().token();
  }

  HttpEntity<?> getHttpEntityForToken(String token) {
    // Used to set the headers and/or body of the request

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);

    return new HttpEntity<>(headers);
  }

  <T> HttpEntity<T> getEntityForToken(String token, T body) {
    // Used to set the headers and/or body of the request

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);

    return new HttpEntity<T>(body, headers);
  }

  void executeClientOperations(String email, Store storeData) {
    String token = logInAndGetToken(email);
    String storeName = storeData.getName();

    // * Search for a store
    // * Get store data
    // * Get producs
    // * Place order

    // Search for a store
    ResponseEntity<List<StoreDTO>> storesResponse =
        restTemplate.exchange(
            "/api/v1/stores?name={name}",
            HttpMethod.GET,
            getHttpEntityForToken(token),
            new ParameterizedTypeReference<List<StoreDTO>>() {},
            storeName);

    List<StoreDTO> stores = storesResponse.getBody();

    UUID storeId = stores.get(0).getId();

    // Get store data
    restTemplate.exchange(
        "/api/v1/stores/{id}",
        HttpMethod.GET,
        getHttpEntityForToken(token),
        StoreDTO.class,
        storeId);

    // Get products
    ResponseEntity<List<ProductDTO>> productsResponse =
        restTemplate.exchange(
            "/api/v1/stores/{id}/products",
            HttpMethod.GET,
            getHttpEntityForToken(token),
            new ParameterizedTypeReference<List<ProductDTO>>() {},
            storeId);

    List<ProductDTO> products = productsResponse.getBody();

    // Place order
    Map<UUID, Integer> orderBody =
        products.stream().collect(Collectors.toMap(product -> product.getId(), product -> 2));

    ResponseEntity<OrderDTO> orderResponse =
        restTemplate.exchange(
            "/api/v1/orders", HttpMethod.POST, getEntityForToken(token, orderBody), OrderDTO.class);
  }

  void executeStoreOperations(String email, Store storeData) {
    String token = logInAndGetToken(email);
    UUID storeId = storeData.getId();

    // * Get store
    // * Update storefront
    // * Get orders
    // * Confirm all orders

    // Get store
    ResponseEntity<StoreDTO> storeResponse =
        restTemplate.exchange(
            "/api/v1/stores/{id}",
            HttpMethod.GET,
            getHttpEntityForToken(token),
            StoreDTO.class,
            storeId);

    StoreDTO store = storeResponse.getBody();

    // Update storefront
    StorefrontDTO storefrontDto = store.getStorefront();
    storefrontDto.setPrimaryColor("#888888");

    restTemplate.exchange(
        "/api/v1/stores/{id}",
        HttpMethod.GET,
        getEntityForToken(token, storefrontDto),
        StoreDTO.class,
        storeId);

    // Get orders
    ResponseEntity<List<OrderDTO>> ordersResponse =
        restTemplate.exchange(
            "/api/v1/orders",
            HttpMethod.GET,
            getHttpEntityForToken(token),
            new ParameterizedTypeReference<List<OrderDTO>>() {});

    List<OrderDTO> orders = ordersResponse.getBody();

    // Confirm orders
    for (OrderDTO order : orders) {
      if (!order.getOrderStatus().equals(OrderStatus.PENDING)) continue;

      restTemplate.exchange(
          "/api/v1/orders/{id}/confirm",
          HttpMethod.PATCH,
          getHttpEntityForToken(token),
          Void.class,
          order.getId());
    }
  }
}
