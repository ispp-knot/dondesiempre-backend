package ispp.project.dondesiempre.repositories;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ispp.project.dondesiempre.models.User;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.models.products.ProductType;
import ispp.project.dondesiempre.models.storefronts.Storefront;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.repositories.products.ProductRepository;
import ispp.project.dondesiempre.repositories.products.ProductTypeRepository;
import ispp.project.dondesiempre.repositories.storefronts.StorefrontRepository;
import ispp.project.dondesiempre.repositories.stores.StoreRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ProductRepositoryTest {

  @Autowired private ProductRepository productRepository;
  @Autowired private ProductTypeRepository productTypeRepository;
  @Autowired private StoreRepository storeRepository;
  @Autowired private StorefrontRepository storefrontRepository;
  @Autowired private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    User user = new User();
    user.setEmail("product-repo-test@test.com");
    user.setPassword("password");
    userRepository.save(user);

    Storefront storefront = new Storefront();
    storefrontRepository.save(storefront);

    GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);
    Store store = new Store();
    store.setName("Test Store");
    store.setEmail("test@test.com");
    store.setStoreID("test-store-id");
    store.setAddress("Test address");
    store.setOpeningHours("9-5");
    store.setAcceptsShipping(false);
    store.setLocation(gf.createPoint(new Coordinate(0.0, 0.0)));
    store.setStorefront(storefront);
    store.setUser(user);
    storeRepository.save(store);

    ProductType type = new ProductType();
    type.setType("Test Type");
    type = productTypeRepository.save(type);

    Product discounted = new Product();
    discounted.setName("Discounted");
    discounted.setPriceInCents(1000);
    discounted.setDiscountedPriceInCents(800);
    discounted.setType(type);
    discounted.setStore(store);
    productRepository.save(discounted);

    Product notDiscounted = new Product();
    notDiscounted.setName("Not Discounted");
    notDiscounted.setPriceInCents(1000);
    notDiscounted.setDiscountedPriceInCents(1000);
    notDiscounted.setType(type);
    notDiscounted.setStore(store);
    productRepository.save(notDiscounted);
  }

  @Test
  public void shouldOnlyReturnDiscountedProducts() {
    List<Product> discountedProducts = productRepository.findAllDiscountedProducts();

    assertNotNull(discountedProducts);
    assertFalse(discountedProducts.isEmpty());
    for (Product product : discountedProducts) {
      assertTrue(product.getDiscountedPriceInCents() < product.getPriceInCents());
    }
  }
}
