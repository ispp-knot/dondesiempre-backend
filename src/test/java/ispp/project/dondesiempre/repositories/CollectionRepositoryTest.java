package ispp.project.dondesiempre.repositories;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ispp.project.dondesiempre.models.User;
import ispp.project.dondesiempre.models.collections.ProductCollection;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.models.products.ProductType;
import ispp.project.dondesiempre.models.storefronts.Storefront;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.repositories.collections.CollectionRepository;
import ispp.project.dondesiempre.repositories.products.ProductRepository;
import ispp.project.dondesiempre.repositories.products.ProductTypeRepository;
import ispp.project.dondesiempre.repositories.storefronts.StorefrontRepository;
import ispp.project.dondesiempre.repositories.stores.StoreRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class CollectionRepositoryTest {

  @Autowired private CollectionRepository collectionRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private ProductTypeRepository productTypeRepository;
  @Autowired private StoreRepository storeRepository;
  @Autowired private StorefrontRepository storefrontRepository;
  @Autowired private UserRepository userRepository;

  private Store createStore() {
    Storefront storefront = new Storefront();
    storefrontRepository.save(storefront);

    User user = new User();
    user.setEmail("store-" + UUID.randomUUID() + "@test.com");
    user.setPassword("test-password");
    userRepository.save(user);

    GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);
    Point location = gf.createPoint(new Coordinate(-5.9845, 37.3891));

    Store store = new Store();
    store.setName("Tienda Test");
    store.setEmail("test@tienda.com");
    store.setStoreID("STORE-TEST-001");
    store.setLocation(location);
    store.setAddress("Calle Test 1");
    store.setOpeningHours("L-V 10:00-20:00");
    store.setAcceptsShipping(false);
    store.setStorefront(storefront);
    store.setUser(user);
    return storeRepository.save(store);
  }

  private Product createProduct(Store store) {
    ProductType type = new ProductType();
    type.setType("Camiseta");
    ProductType savedType = productTypeRepository.save(type);

    Product product = new Product();
    product.setName("Camiseta Basica");
    product.setPriceInCents(1999);
    product.setDiscountedPriceInCents(1799);
    product.setDescription("Algodon");
    product.setType(savedType);
    product.setStore(store);
    return productRepository.save(product);
  }

  @Test
  void shouldReturnCollections_WhenFindByStoreId() {
    Store store = createStore();
    Product product = createProduct(store);

    ProductCollection collection = new ProductCollection();
    collection.setName("Primavera");
    collection.setStore(store);
    collection.setProducts(Set.of(product));
    collectionRepository.save(collection);

    List<ProductCollection> collections = collectionRepository.findByStoreId(store.getId());

    assertFalse(collections.isEmpty());
    assertTrue(collections.stream().allMatch(c -> c.getStore().getId().equals(store.getId())));
  }

  @Test
  void shouldReturnTrue_WhenNameExistsInStore() {
    Store store = createStore();
    Product product = createProduct(store);

    ProductCollection collection = new ProductCollection();
    collection.setName("Accesorios");
    collection.setStore(store);
    collection.setProducts(Set.of(product));
    collectionRepository.save(collection);

    assertTrue(collectionRepository.existsByNameAndStoreId("Accesorios", store.getId()));
  }

  @Test
  void shouldReturnFalse_WhenNameDoesNotExistInStore() {
    Store store = createStore();

    assertFalse(collectionRepository.existsByNameAndStoreId("Inexistente", store.getId()));
  }
}
