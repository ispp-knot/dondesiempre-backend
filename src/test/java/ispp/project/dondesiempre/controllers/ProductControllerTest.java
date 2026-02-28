package ispp.project.dondesiempre.controllers;

import static org.junit.jupiter.api.Assertions.assertThrows;

import ispp.project.dondesiempre.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.models.User;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.models.products.ProductType;
import ispp.project.dondesiempre.models.products.dto.DiscountModificationDTO;
import ispp.project.dondesiempre.models.products.dto.ProductCreationDTO;
import ispp.project.dondesiempre.models.products.dto.ProductDTO;
import ispp.project.dondesiempre.models.storefronts.Storefront;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.repositories.UserRepository;
import ispp.project.dondesiempre.repositories.products.ProductTypeRepository;
import ispp.project.dondesiempre.repositories.stores.StoreRepository;
import ispp.project.dondesiempre.services.UserService;
import java.util.List;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Transactional
public class ProductControllerTest {

  @Autowired private ProductController productController;
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

  private User getTestUser() {
    return testUser;
  }

  @Test
  public void shouldCreateNewProduct() {
    // Create and save a product type

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
    store.setUser(getTestUser());

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

    Product product = productController.createProduct(dto).getBody();
    assert product != null;
    assert product.getId() != null;
    assert product.getName().equals(dto.getName());
  }

  @Test
  public void shouldThrowInvalidRequestException_WhenDiscountedPriceIsGreaterThanOriginalPrice() {
    // Create and save a product type

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
    store.setUser(getTestUser());

    Store saved_store = storeRepository.save(store);

    ProductType type = new ProductType();
    type.setType("Test Product Type");
    ProductType savedProductType = productTypeRepository.save(type);

    ProductCreationDTO dto = new ProductCreationDTO();
    dto.setName("Test Product");
    dto.setPriceInCents(1000);
    dto.setDiscountedPriceInCents(1200); // Invalid discounted price
    dto.setDescription("This is a test product");
    dto.setTypeId(savedProductType.getId());
    dto.setStoreId(saved_store.getId());

    assertThrows(
        InvalidRequestException.class,
        () -> {
          productController.createProduct(dto);
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
    store.setUser(getTestUser());

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

    Product product = productController.createProduct(dto).getBody();

    DiscountModificationDTO discount = new DiscountModificationDTO();
    discount.setDiscountedPriceInCents(700);
    ResponseEntity<Product> response = productController.updateDiscount(product.getId(), discount);
    assert response.getStatusCode() == HttpStatus.ACCEPTED;
    Product updatedProduct = response.getBody();
    assert updatedProduct != null;
    assert updatedProduct.getDiscountedPriceInCents() == 700;
  }

  @Test
  public void shouldThrowResourceNotFoundException_WhenUpdatingDiscountForNonExistentProduct() {
    DiscountModificationDTO discount = new DiscountModificationDTO();
    discount.setDiscountedPriceInCents(500);
    assertThrows(
        ResourceNotFoundException.class,
        () -> {
          productController.updateDiscount(9999, discount);
        });
  }

  @Test
  public void shouldThrowInvalidRequestException_WhenUpdatingDiscountToGreaterThanOriginalPrice() {
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
    store.setUser(getTestUser());

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

    Product product = productController.createProduct(dto).getBody();
    DiscountModificationDTO discount = new DiscountModificationDTO();
    discount.setDiscountedPriceInCents(1200); // Invalid discounted price
    assertThrows(
        InvalidRequestException.class,
        () -> {
          productController.updateDiscount(product.getId(), discount);
        });
  }

  @Test
  public void shouldThrowResourceNotFoundException_WhenGettingNonExistentProduct() {
    assertThrows(
        ResourceNotFoundException.class,
        () -> {
          productController.getProductById(9999);
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
    store.setUser(getTestUser());

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

    Product product = productController.createProduct(dto).getBody();

    ResponseEntity<ProductDTO> response = productController.getProductById(product.getId());
    assert response.getStatusCode() == HttpStatus.OK;
    ProductDTO productDTO = response.getBody();
    assert productDTO != null;
    assert productDTO.getName().equals(dto.getName());
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
    store.setUser(getTestUser());

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

    productController.createProduct(dto);
    dto.setName("Test Product 2");
    productController.createProduct(dto);

    ResponseEntity<List<ProductDTO>> response = productController.getAllProducts();
    assert response.getStatusCode() == HttpStatus.OK;
    List<ProductDTO> products = response.getBody();
    assert products != null;
    assert products.size() >= 2;
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
    store.setUser(getTestUser());

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

    productController.createProduct(dto);
    dto.setName("Test Product 2");
    dto.setDiscountedPriceInCents(1000);
    productController.createProduct(dto);

    ResponseEntity<List<ProductDTO>> response = productController.getDiscountedProducts();
    assert response.getStatusCode() == HttpStatus.OK;
    List<ProductDTO> products = response.getBody();
    assert products != null;
    assert products.size() >= 1;
  }
}
