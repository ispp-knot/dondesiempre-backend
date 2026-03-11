package ispp.project.dondesiempre.modules.products.services;

import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.auth.repositories.UserRepository;
import ispp.project.dondesiempre.modules.auth.services.AuthService;
import ispp.project.dondesiempre.modules.products.dtos.ProductCreationDTO;
import ispp.project.dondesiempre.modules.products.models.Product;
import ispp.project.dondesiempre.modules.products.models.ProductType;
import ispp.project.dondesiempre.modules.products.repositories.ProductTypeRepository;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.models.Storefront;
import ispp.project.dondesiempre.modules.stores.repositories.StoreRepository;
import ispp.project.dondesiempre.utils.cloudinary.CloudinaryService;
import ispp.project.dondesiempre.utils.cloudinary.CoordinatesService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

  @Autowired
  private ProductService productService;
  @Autowired
  private ProductTypeRepository productTypeRepository;
  @Autowired
  private StoreRepository storeRepository;
  @Autowired
  private UserRepository userRepository;
  @MockitoBean
  private AuthService authService;
  @MockitoBean
  private CloudinaryService cloudinaryService;
  @Autowired
  private CoordinatesService coordinatesService;

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
    store.setLocation(coordinatesService.createPoint(0.0, 0.0));
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
    dto.setDescription("This is a test product");
    dto.setTypeId(savedProductType.getId());

    Product product = productService.createProduct(dto, null, saved_store.getId());
    assert product != null;
    assert product.getName().equals("Test Product");
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
    store.setUser(testUser);

    Store saved_store = storeRepository.save(store);

    ProductType type = new ProductType();
    type.setType("Test Product Type");
    ProductType savedProductType = productTypeRepository.save(type);

    ProductCreationDTO dto = new ProductCreationDTO();
    dto.setName("Test Product");
    dto.setPriceInCents(1000);
    dto.setDescription("This is a test product");
    dto.setTypeId(savedProductType.getId());

    Product product = productService.createProduct(dto, null, saved_store.getId());
    Product updatedProduct = productService.updateProductDiscount(product.getId(), 700);
    assert updatedProduct.getDiscountedPriceInCents().get() == 700;
    assert updatedProduct.getId().equals(product.getId());
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
    store.setUser(testUser);

    Store saved_store = storeRepository.save(store);

    ProductType type = new ProductType();
    type.setType("Test Product Type");
    ProductType savedProductType = productTypeRepository.save(type);

    ProductCreationDTO dto = new ProductCreationDTO();
    dto.setName("Test Product");
    dto.setPriceInCents(1000);
    dto.setDescription("This is a test product");
    dto.setTypeId(savedProductType.getId());

    Product product = productService.createProduct(dto, null, saved_store.getId());
    Product fetchedProduct = productService.getProductById(product.getId());
    assert fetchedProduct != null;
    assert fetchedProduct.getId().equals(product.getId());
    assert fetchedProduct.getName().equals("Test Product");
  }

  @Test
  public void shouldThrowException_WhenGettingNonExistentProduct() {
    UUID id = UUID.randomUUID();
    try {
      productService.getProductById(id);
      assert false;
    } catch (RuntimeException e) {
      assert e.getMessage().equals("Product not found with id: " + id);
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
    store.setLocation(coordinatesService.createPoint(0.0, 0.0));
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
    dto.setDescription("This is a test product");
    dto.setTypeId(savedProductType.getId());

    productService.createProduct(dto, null, saved_store.getId());
    dto.setName("Another Test Product");
    productService.createProduct(dto, null, saved_store.getId());

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
    store.setLocation(coordinatesService.createPoint(0.0, 0.0));
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
    dto.setDescription("This is a test product");
    dto.setTypeId(savedProductType.getId());

    Product product = productService.createProduct(dto, null, saved_store.getId());
    productService.updateProductDiscount(product.getId(), 20);
    dto.setName("Non-discounted Product");
    productService.createProduct(dto, null, saved_store.getId());

    assert productService.getAllDiscountedProducts().size() >= 1;
  }
}
