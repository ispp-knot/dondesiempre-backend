package ispp.project.dondesiempre.services;

import static org.junit.jupiter.api.Assertions.assertThrows;

import ispp.project.dondesiempre.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.models.User;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.models.products.ProductType;
import ispp.project.dondesiempre.models.products.dto.ProductCreationDTO;
import ispp.project.dondesiempre.models.storefronts.Storefront;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.repositories.UserRepository;
import ispp.project.dondesiempre.repositories.products.ProductTypeRepository;
import ispp.project.dondesiempre.repositories.stores.StoreRepository;
import ispp.project.dondesiempre.services.products.ProductService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Transactional
public class ProductServiceTest {

  @Autowired private ProductService productService;
  @Autowired private ProductTypeRepository productTypeRepository;
  @Autowired private StoreRepository storeRepository;
  @Autowired private UserRepository userRepository;
  @MockitoBean private UserService userService;

  private User testUser;

  @BeforeEach
  void setUp() {
    User user = new User();
    user.setEmail("test-owner@test.com");
    user.setPassword("password");
    testUser = userRepository.save(user);
  }

  @Test
  public void shouldCreateNewProduct() {

    Storefront storefront = new Storefront();
    storefront.setIsFirstCollections(true);
    storefront.setPrimaryColor("#c65a3a");
    storefront.setSecondaryColor("#19756a");

    Store store = new Store();
    store.setName("Test Store");
    store.setEmail("test@example.com");
    store.setStoreID("test-store");
    store.setLocation(
        new Point(
            new CoordinateArraySequence(new Coordinate[] {new Coordinate(0.0, 0.0)}),
            new GeometryFactory(new PrecisionModel(PrecisionModel.FIXED), 0)));
    store.setAddress("123 Test Street");
    store.setOpeningHours("9am - 5pm");
    store.setAcceptsShipping(true);
    store.setStorefront(storefront);
    store.setUser(testUser);

    Store saved_store = storeRepository.save(store);

    ProductType type = new ProductType();
    type.setType("Test Product Type");
    ProductType savedProductType = productTypeRepository.save(type);

    ProductCreationDTO dto = new ProductCreationDTO();
    dto.setName("Test Product");
    dto.setPriceInCents(1000);
    dto.setDiscountedPriceInCents(800);
    dto.setDescription("This is a test product");
    dto.setTypeId(savedProductType.getId());
    dto.setStoreId(saved_store.getId());

    Product product = productService.saveProduct(dto);
    assert product != null;
    assert product.getName().equals("Test Product");
  }

  @Test
  public void shouldThrowException_WhenDiscountedPriceIsGreaterThanOriginalPrice() {

    Storefront storefront = new Storefront();
    storefront.setIsFirstCollections(true);
    storefront.setPrimaryColor("#c65a3a");
    storefront.setSecondaryColor("#19756a");

    Store store = new Store();
    store.setName("Test Store");
    store.setEmail("test@example.com");
    store.setStoreID("test-store");
    store.setLocation(
        new Point(
            new CoordinateArraySequence(new Coordinate[] {new Coordinate(0.0, 0.0)}),
            new GeometryFactory(new PrecisionModel(PrecisionModel.FIXED), 0)));
    store.setAddress("123 Test Street");
    store.setOpeningHours("9am - 5pm");
    store.setAcceptsShipping(true);
    store.setStorefront(storefront);
    store.setUser(testUser);

    Store saved_store = storeRepository.save(store);

    ProductType type = new ProductType();
    type.setType("Test Product Type");
    ProductType savedProductType = productTypeRepository.save(type);

    ProductCreationDTO dto = new ProductCreationDTO();
    dto.setName("Test Product");
    dto.setPriceInCents(1000);
    dto.setDiscountedPriceInCents(1200); // This is greater than the original pricce
    dto.setDescription("This is a test product");
    dto.setTypeId(savedProductType.getId());
    dto.setStoreId(saved_store.getId());

    assertThrows(
        InvalidRequestException.class,
        () -> {
          productService.saveProduct(dto);
        });
  }

  @Test
  public void shouldUpdateProductDiscount() {
    Storefront storefront = new Storefront();
    storefront.setIsFirstCollections(true);
    storefront.setPrimaryColor("#c65a3a");
    storefront.setSecondaryColor("#19756a");

    Store store = new Store();
    store.setName("Test Store");
    store.setEmail("test@example.com");
    store.setStoreID("test-store");
    store.setLocation(
        new Point(
            new CoordinateArraySequence(new Coordinate[] {new Coordinate(0.0, 0.0)}),
            new GeometryFactory(new PrecisionModel(PrecisionModel.FIXED), 0)));
    store.setAddress("123 Test Street");
    store.setOpeningHours("9am - 5pm");
    store.setAcceptsShipping(true);
    store.setStorefront(storefront);
    store.setUser(testUser);

    Store saved_store = storeRepository.save(store);

    ProductType type = new ProductType();
    type.setType("Test Product Type");
    ProductType savedProductType = productTypeRepository.save(type);

    ProductCreationDTO dto = new ProductCreationDTO();
    dto.setName("Test Product");
    dto.setPriceInCents(1000);
    dto.setDiscountedPriceInCents(800);
    dto.setDescription("This is a test product");
    dto.setTypeId(savedProductType.getId());
    dto.setStoreId(saved_store.getId());

    Product product = productService.saveProduct(dto);
    Product updatedProduct = productService.updateProductDiscount(product.getId(), 700);
    assert updatedProduct.getDiscountedPriceInCents() == 700;
    assert updatedProduct.getId().equals(product.getId());
  }

  @Test
  public void shouldThrowException_WhenUpdatingDiscountToGreaterThanOriginalPrice() {

    Storefront storefront = new Storefront();
    storefront.setIsFirstCollections(true);
    storefront.setPrimaryColor("#c65a3a");
    storefront.setSecondaryColor("#19756a");

    Store store = new Store();
    store.setName("Test Store");
    store.setEmail("test@example.com");
    store.setStoreID("test-store");
    store.setLocation(
        new Point(
            new CoordinateArraySequence(new Coordinate[] {new Coordinate(0.0, 0.0)}),
            new GeometryFactory(new PrecisionModel(PrecisionModel.FIXED), 0)));
    store.setAddress("123 Test Street");
    store.setOpeningHours("9am - 5pm");
    store.setAcceptsShipping(true);
    store.setStorefront(storefront);
    store.setUser(testUser);

    Store saved_store = storeRepository.save(store);

    ProductType type = new ProductType();
    type.setType("Test Product Type");
    ProductType savedProductType = productTypeRepository.save(type);

    ProductCreationDTO dto = new ProductCreationDTO();
    dto.setName("Test Product");
    dto.setPriceInCents(1000);
    dto.setDiscountedPriceInCents(800);
    dto.setDescription("This is a test product");
    dto.setTypeId(savedProductType.getId());
    dto.setStoreId(saved_store.getId());

    Product product = productService.saveProduct(dto);
    assertThrows(
        InvalidRequestException.class,
        () -> {
          productService.updateProductDiscount(
              product.getId(), 1200); // This is greater than the original price
        });
  }

  @Test
  public void shouldGetProductById() {
    Storefront storefront = new Storefront();
    storefront.setIsFirstCollections(true);
    storefront.setPrimaryColor("#c65a3a");
    storefront.setSecondaryColor("#19756a");

    Store store = new Store();
    store.setName("Test Store");
    store.setEmail("test@example.com");
    store.setStoreID("test-store");
    store.setLocation(
        new Point(
            new CoordinateArraySequence(new Coordinate[] {new Coordinate(0.0, 0.0)}),
            new GeometryFactory(new PrecisionModel(PrecisionModel.FIXED), 0)));
    store.setAddress("123 Test Street");
    store.setOpeningHours("9am - 5pm");
    store.setAcceptsShipping(true);
    store.setStorefront(storefront);
    store.setUser(testUser);

    Store saved_store = storeRepository.save(store);

    ProductType type = new ProductType();
    type.setType("Test Product Type");
    ProductType savedProductType = productTypeRepository.save(type);

    ProductCreationDTO dto = new ProductCreationDTO();
    dto.setName("Test Product");
    dto.setPriceInCents(1000);
    dto.setDiscountedPriceInCents(800);
    dto.setDescription("This is a test product");
    dto.setTypeId(savedProductType.getId());
    dto.setStoreId(saved_store.getId());

    Product product = productService.saveProduct(dto);
    Product fetchedProduct = productService.getProductById(product.getId());
    assert fetchedProduct != null;
    assert fetchedProduct.getId().equals(product.getId());
    assert fetchedProduct.getName().equals("Test Product");
  }

  @Test
  public void shouldThrowException_WhenGettingNonExistentProduct() {
    try {
      productService.getProductById(UUID.randomUUID());
      assert false;
    } catch (RuntimeException e) {
      assert e.getMessage().equals("Product not found");
    }
  }

  @Test
  public void shouldGetAllProducts() {
    Storefront storefront = new Storefront();
    storefront.setIsFirstCollections(true);
    storefront.setPrimaryColor("#c65a3a");
    storefront.setSecondaryColor("#19756a");

    Store store = new Store();
    store.setName("Test Store");
    store.setEmail("test@example.com");
    store.setStoreID("test-store");
    store.setLocation(
        new Point(
            new CoordinateArraySequence(new Coordinate[] {new Coordinate(0.0, 0.0)}),
            new GeometryFactory(new PrecisionModel(PrecisionModel.FIXED), 0)));
    store.setAddress("123 Test Street");
    store.setOpeningHours("9am - 5pm");
    store.setAcceptsShipping(true);
    store.setStorefront(storefront);
    store.setUser(testUser);

    Store saved_store = storeRepository.save(store);

    ProductType type = new ProductType();
    type.setType("Test Product Type");
    ProductType savedProductType = productTypeRepository.save(type);

    ProductCreationDTO dto = new ProductCreationDTO();
    dto.setName("Test Product");
    dto.setPriceInCents(1000);
    dto.setDiscountedPriceInCents(800);
    dto.setDescription("This is a test product");
    dto.setTypeId(savedProductType.getId());
    dto.setStoreId(saved_store.getId());

    productService.saveProduct(dto);
    dto.setName("Another Test Product");
    productService.saveProduct(dto);

    assert productService.getAllProducts().size() >= 2;
  }

  @Test
  public void shouldGetAllDiscountedProducts() {
    Storefront storefront = new Storefront();
    storefront.setIsFirstCollections(true);
    storefront.setPrimaryColor("#c65a3a");
    storefront.setSecondaryColor("#19756a");

    Store store = new Store();
    store.setName("Test Store");
    store.setEmail("test@example.com");
    store.setStoreID("test-store");
    store.setLocation(
        new Point(
            new CoordinateArraySequence(new Coordinate[] {new Coordinate(0.0, 0.0)}),
            new GeometryFactory(new PrecisionModel(PrecisionModel.FIXED), 0)));
    store.setAddress("123 Test Street");
    store.setOpeningHours("9am - 5pm");
    store.setAcceptsShipping(true);
    store.setStorefront(storefront);
    store.setUser(testUser);

    Store saved_store = storeRepository.save(store);

    ProductType type = new ProductType();
    type.setType("Test Product Type");
    ProductType savedProductType = productTypeRepository.save(type);

    ProductCreationDTO dto = new ProductCreationDTO();
    dto.setName("Test Product");
    dto.setPriceInCents(1000);
    dto.setDiscountedPriceInCents(800);
    dto.setDescription("This is a test product");
    dto.setTypeId(savedProductType.getId());
    dto.setStoreId(saved_store.getId());

    productService.saveProduct(dto);
    dto.setName("Non-discounted Product");
    dto.setDiscountedPriceInCents(1000); // No discount
    productService.saveProduct(dto);

    assert productService.getAllDiscountedProducts().size() >= 1;
  }
}
