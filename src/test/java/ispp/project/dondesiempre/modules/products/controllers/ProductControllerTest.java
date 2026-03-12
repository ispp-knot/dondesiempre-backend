package ispp.project.dondesiempre.modules.products.controllers;

import static org.junit.jupiter.api.Assertions.assertThrows;

import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.auth.repositories.UserRepository;
import ispp.project.dondesiempre.modules.auth.services.AuthService;
import ispp.project.dondesiempre.modules.common.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.products.dtos.ProductCreationDTO;
import ispp.project.dondesiempre.modules.products.dtos.ProductDTO;
import ispp.project.dondesiempre.modules.products.dtos.ProductDiscountUpdateDTO;
import ispp.project.dondesiempre.modules.products.models.Product;
import ispp.project.dondesiempre.modules.products.models.ProductType;
import ispp.project.dondesiempre.modules.products.repositories.ProductTypeRepository;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.models.Storefront;
import ispp.project.dondesiempre.modules.stores.repositories.StoreRepository;
import ispp.project.dondesiempre.utils.cloudinary.CloudinaryService;
import ispp.project.dondesiempre.utils.cloudinary.CoordinatesService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
  @MockitoBean private AuthService authService;
  @MockitoBean private CloudinaryService cloudinaryService;
  @Autowired private CoordinatesService coordinatesService;

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
    store.setLocation(coordinatesService.createPoint(0.0, 0.0));
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
    dto.setDescription("This is a test product");
    dto.setTypeId(savedProductType.getId());

    Product product = productController.createProduct(dto, null, saved_store.getId()).getBody();
    assert product != null;
    assert product.getId() != null;
    assert product.getName().equals(dto.getName());
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
    store.setLocation(coordinatesService.createPoint(0.0, 0.0));
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
    dto.setDescription("This is a test product");
    dto.setTypeId(savedProductType.getId());

    Product product = productController.createProduct(dto, null, saved_store.getId()).getBody();

    ProductDiscountUpdateDTO discount = new ProductDiscountUpdateDTO();
    discount.setDiscountPercentage(70);
    ResponseEntity<Product> response = productController.updateDiscount(product.getId(), discount);
    assert response.getStatusCode() == HttpStatus.ACCEPTED;
    Product updatedProduct = response.getBody();
    assert updatedProduct != null;
    assert updatedProduct.getDiscountPercentage().get() == 70;
  }

  @Test
  public void shouldThrowResourceNotFoundException_WhenUpdatingDiscountForNonExistentProduct() {
    ProductDiscountUpdateDTO discount = new ProductDiscountUpdateDTO();
    discount.setDiscountPercentage(50);

    UUID nonExistentId = UUID.randomUUID();

    assertThrows(
        ResourceNotFoundException.class,
        () -> {
          productController.updateDiscount(nonExistentId, discount);
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
    store.setLocation(coordinatesService.createPoint(0.0, 0.0));
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
    dto.setDescription("This is a test product");
    dto.setTypeId(savedProductType.getId());

    Product product = productController.createProduct(dto, null, saved_store.getId()).getBody();
    ProductDiscountUpdateDTO discount = new ProductDiscountUpdateDTO();
    discount.setDiscountPercentage(1200); // Invalid discounted price
    assertThrows(
        InvalidRequestException.class,
        () -> {
          productController.updateDiscount(product.getId(), discount);
        });
  }

  @Test
  public void shouldThrowResourceNotFoundException_WhenGettingNonExistentProduct() {
    UUID nonExistentId = UUID.randomUUID();
    assertThrows(
        ResourceNotFoundException.class,
        () -> {
          productController.getProductById(nonExistentId);
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
    store.setLocation(coordinatesService.createPoint(0.0, 0.0));
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
    dto.setDescription("This is a test product");
    dto.setTypeId(savedProductType.getId());

    Product product = productController.createProduct(dto, null, saved_store.getId()).getBody();

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
    store.setLocation(coordinatesService.createPoint(0.0, 0.0));
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
    dto.setDescription("This is a test product");
    dto.setTypeId(savedProductType.getId());

    productController.createProduct(dto, null, saved_store.getId());
    dto.setName("Test Product 2");
    productController.createProduct(dto, null, saved_store.getId());

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
    store.setLocation(coordinatesService.createPoint(0.0, 0.0));
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
    dto.setDescription("This is a test product");
    dto.setTypeId(savedProductType.getId());

    var product = productController.createProduct(dto, null, saved_store.getId());
    dto.setName("Test Product 2");
    productController.createProduct(dto, null, saved_store.getId());

    ProductDiscountUpdateDTO discountModificationDTO = new ProductDiscountUpdateDTO();
    discountModificationDTO.setDiscountPercentage(20);
    productController.updateDiscount(product.getBody().getId(), discountModificationDTO);
    ResponseEntity<List<ProductDTO>> response = productController.getDiscountedProducts();
    assert response.getStatusCode() == HttpStatus.OK;
    List<ProductDTO> products = response.getBody();
    assert products != null;
    assert products.size() >= 1;
  }
}
