package ispp.project.dondesiempre.modules.products.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import java.util.Optional;
import java.util.UUID;
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
    store.setLocation(coordinatesService.createPoint(0.0, 0.0));
    store.setStorefront(storefront);
    store.setUser(user);
    store.setAccountId("acc_AAAAA");
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
    notDiscounted.setDeleted(false);
    productRepository.save(notDiscounted);
  }

  @Test
  public void shouldOnlyReturnDiscountedProducts() {
    List<Product> discountedProducts =
        productRepository.findByDiscountPercentageIsNotNullAndIsDeletedIsFalse();

    assertNotNull(discountedProducts);
    assertFalse(discountedProducts.isEmpty());
    for (Product product : discountedProducts) {
      assertTrue(product.getDiscountPercentage().isPresent());
    }
  }

  @Test
  public void shouldFindProductsByStoreIdAndNotDeleted() {
    Store store = storeRepository.findAll().get(0);
    List<Product> products = productRepository.findByStoreIdAndIsDeletedIsFalse(store.getId());

    assertNotNull(products);
    assertFalse(products.isEmpty());
    for (Product product : products) {
      assertEquals(store.getId(), product.getStore().getId());
      assertFalse(product.isDeleted());
    }
  }

  @Test
  public void shouldNotReturnDeletedProductsWhenFindByStoreId() {
    Store store = storeRepository.findAll().get(0);
    List<Product> initialProducts =
        productRepository.findByStoreIdAndIsDeletedIsFalse(store.getId());
    int initialCount = initialProducts.size();

    if (!initialProducts.isEmpty()) {
      Product productToDelete = initialProducts.get(0);
      productToDelete.setDeleted(true);
      productRepository.save(productToDelete);

      List<Product> updatedProducts =
          productRepository.findByStoreIdAndIsDeletedIsFalse(store.getId());
      assertEquals(initialCount - 1, updatedProducts.size());
    }
  }

  @Test
  public void shouldFindProductByIdAndNotDeleted() {
    List<Product> allProducts = productRepository.findByIsDeletedIsFalse();
    if (!allProducts.isEmpty()) {
      Product product = allProducts.get(0);
      Optional<Product> foundProduct =
          productRepository.findByIdAndIsDeletedIsFalse(product.getId());

      assertTrue(foundProduct.isPresent());
      assertEquals(product.getId(), foundProduct.get().getId());
      assertFalse(foundProduct.get().isDeleted());
    }
  }

  @Test
  public void shouldNotFindDeletedProductById() {
    List<Product> allProducts = productRepository.findByIsDeletedIsFalse();
    if (!allProducts.isEmpty()) {
      Product product = allProducts.get(0);
      product.setDeleted(true);
      productRepository.save(product);

      Optional<Product> foundProduct =
          productRepository.findByIdAndIsDeletedIsFalse(product.getId());
      assertFalse(foundProduct.isPresent());
    }
  }

  @Test
  public void shouldFindAllNonDeletedProducts() {
    List<Product> allProducts = productRepository.findByIsDeletedIsFalse();

    assertNotNull(allProducts);
    assertFalse(allProducts.isEmpty());
    for (Product product : allProducts) {
      assertFalse(product.isDeleted());
    }
  }

  @Test
  public void shouldReturnEmptyListForNonExistentStore() {
    UUID nonExistentStoreId = UUID.randomUUID();
    List<Product> products = productRepository.findByStoreIdAndIsDeletedIsFalse(nonExistentStoreId);

    assertNotNull(products);
    assertTrue(products.isEmpty());
  }

  @Test
  public void shouldReturnEmptyOptionalForNonExistentProductId() {
    UUID nonExistentId = UUID.randomUUID();
    Optional<Product> product = productRepository.findByIdAndIsDeletedIsFalse(nonExistentId);

    assertFalse(product.isPresent());
  }

  @Test
  public void shouldFindProductsByPromotionIdReturnsEmptyForNonExistent() {
    UUID nonExistentPromotionId = UUID.randomUUID();
    List<Product> products = productRepository.findProductsByPromotionId(nonExistentPromotionId);

    assertNotNull(products);
    assertTrue(products.isEmpty());
  }

  @Test
  public void shouldFindOutfitProductsByOutfitIdReturnsEmptyForNonExistent() {
    UUID nonExistentOutfitId = UUID.randomUUID();
    List<Product> products = productRepository.findOutfitProductsByOutfitId(nonExistentOutfitId);

    assertNotNull(products);
    assertTrue(products.isEmpty());
  }
}
