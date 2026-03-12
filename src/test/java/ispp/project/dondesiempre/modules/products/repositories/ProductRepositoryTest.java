package ispp.project.dondesiempre.modules.products.repositories;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ispp.project.dondesiempre.config.coordinates.GeometryFactoryConfig;
import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.auth.repositories.UserRepository;
import ispp.project.dondesiempre.modules.products.models.Product;
import ispp.project.dondesiempre.modules.products.models.ProductType;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.models.Storefront;
import ispp.project.dondesiempre.modules.stores.repositories.StoreRepository;
import ispp.project.dondesiempre.modules.stores.repositories.StorefrontRepository;
import ispp.project.dondesiempre.utils.cloudinary.CoordinatesService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({CoordinatesService.class, GeometryFactoryConfig.class})
public class ProductRepositoryTest {

  @Autowired private ProductRepository productRepository;
  @Autowired private ProductTypeRepository productTypeRepository;
  @Autowired private StoreRepository storeRepository;
  @Autowired private StorefrontRepository storefrontRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private CoordinatesService coordinatesService;

  @BeforeEach
  void setUp() {
    User user = new User();
    user.setEmail("product-repo-test@test.com");
    user.setPassword("password");
    userRepository.save(user);

    Storefront storefront = new Storefront();
    storefrontRepository.save(storefront);

    Store store = new Store();
    store.setName("Test Store");
    store.setEmail("test@test.com");
    store.setAddress("Test address");
    store.setOpeningHours("9-5");
    store.setAcceptsShipping(false);
    store.setLocation(coordinatesService.createPoint(0.0, 0.0));
    store.setStorefront(storefront);
    store.setUser(user);
    storeRepository.save(store);

    ProductType type = new ProductType();
    type.setType("Test Type");
    type = productTypeRepository.save(type);

    Product discounted = new Product();
    discounted.setName("Discounted");
    discounted.setPriceInCents(1000);
    discounted.setDiscountPercentage(12);
    discounted.setType(type);
    discounted.setStore(store);
    productRepository.save(discounted);

    Product notDiscounted = new Product();
    notDiscounted.setName("Not Discounted");
    notDiscounted.setPriceInCents(1000);
    notDiscounted.setDiscountPercentage(32);
    notDiscounted.setType(type);
    notDiscounted.setStore(store);
    productRepository.save(notDiscounted);
  }

  @Test
  public void shouldOnlyReturnDiscountedProducts() {
    List<Product> discountedProducts = productRepository.findByDiscountPercentageIsNotNull();

    assertNotNull(discountedProducts);
    assertFalse(discountedProducts.isEmpty());
    for (Product product : discountedProducts) {
      assertTrue(product.getDiscountPercentage().get() < product.getPriceInCents());
    }
  }
}
