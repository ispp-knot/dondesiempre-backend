package ispp.project.dondesiempre.modules.products.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.auth.repositories.UserRepository;
import ispp.project.dondesiempre.modules.auth.services.AuthService;
import ispp.project.dondesiempre.modules.common.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.common.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.modules.outfits.repositories.OutfitProductRepository;
import ispp.project.dondesiempre.modules.products.dtos.ProductCreationDTO;
import ispp.project.dondesiempre.modules.products.dtos.ProductUpdateDTO;
import ispp.project.dondesiempre.modules.products.models.Product;
import ispp.project.dondesiempre.modules.products.models.ProductType;
import ispp.project.dondesiempre.modules.products.repositories.ProductTypeRepository;
import ispp.project.dondesiempre.modules.promotions.repositories.PromotionProductRepository;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Transactional
public class ProductServiceTest {

  @Autowired private ProductService productService;
  @Autowired private ProductTypeRepository productTypeRepository;
  @Autowired private StoreRepository storeRepository;
  @MockitoBean private OutfitProductRepository outfitProductRepository;
  @MockitoBean private PromotionProductRepository promotionProductRepository;
  @Autowired private UserRepository userRepository;
  @MockitoBean private AuthService authService;
  @MockitoBean private CloudinaryService cloudinaryService;
  @Autowired private CoordinatesService coordinatesService;

  private User testUser;
  private Store saved_store;
  private ProductType savedProductType;
  private ProductCreationDTO dto;

  @BeforeEach
  void setUp() {
    User user = new User();
    user.setEmail("test-owner@test.com");
    user.setPassword("password");
    testUser = userRepository.save(user);

    Storefront storefront = new Storefront();
    storefront.setPrimaryColor("#c65a3a");
    storefront.setSecondaryColor("#19756a");

    Store store = new Store();
    store.setName("Test Store");
    store.setEmail("test@example.com");
    store.setLocation(coordinatesService.createPoint(0.0, 0.0));
    store.setAddress("123 Test Street");
    store.setOpeningHours("9am - 5pm");
    store.setStorefront(storefront);
    store.setUser(testUser);
    store.setAccountId("acc_AAAAA");
    saved_store = storeRepository.save(store);

    ProductType type = new ProductType();
    type.setType("Test Product Type");
    savedProductType = productTypeRepository.save(type);

    dto = new ProductCreationDTO();
    dto.setName("Test Product");
    dto.setPriceInCents(1000);
    dto.setDescription("This is a test product");
    dto.setTypeId(savedProductType.getId());
  }

  private Product createTestProduct() {
    return productService.createProduct(dto, null, saved_store.getId());
  }

  @Test
  public void shouldCreateNewProduct() {
    Product product = createTestProduct();
    assertNotNull(product);
    assertEquals("Test Product", product.getName());
  }

  @Test
  public void shouldUpdateProductDiscount() {
    Product product = createTestProduct();
    Product updatedProduct = productService.updateProductDiscount(product.getId(), 700);
    assertEquals(700, updatedProduct.getDiscountPercentage().get());
    assertEquals(product.getId(), updatedProduct.getId());
  }

  @Test
  public void shouldGetProductById() {
    Product product = createTestProduct();
    Product fetchedProduct = productService.getProductById(product.getId());
    assertNotNull(fetchedProduct);
    assertEquals(product.getId(), fetchedProduct.getId());
    assertEquals("Test Product", fetchedProduct.getName());
  }

  @Test
  public void shouldThrowException_WhenGettingNonExistentProduct() {
    UUID id = UUID.randomUUID();
    assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(id));
  }

  @Test
  public void shouldGetAllProducts() {
    createTestProduct();
    List<Product> products = productService.findAll();
    assertEquals(1, products.size());
  }

  @Test
  public void shouldDeleteProduct() {
    Product product = createTestProduct();
    when(outfitProductRepository.existsByProductId(product.getId())).thenReturn(false);
    when(promotionProductRepository.existsByProductId(product.getId())).thenReturn(false);
    doNothing().when(authService).assertUserOwnsStore(any(Store.class));
    productService.deleteProduct(product.getId());
    assertThrows(
        ResourceNotFoundException.class, () -> productService.getProductById(product.getId()));
  }

  @Test
  public void shouldNotDeleteProduct() {
    Product product = createTestProduct();
    when(outfitProductRepository.existsByProductId(product.getId())).thenReturn(true);
    when(promotionProductRepository.existsByProductId(product.getId())).thenReturn(false);
    doNothing().when(authService).assertUserOwnsStore(any(Store.class));

    assertThrows(
        InvalidRequestException.class, () -> productService.deleteProduct(product.getId()));
  }

  @Test
  public void shouldThrowException_WhenDeletingNonExistentProduct() {
    UUID id = UUID.randomUUID();
    doThrow(new ResourceNotFoundException("Product not found with id: " + id))
        .when(authService)
        .assertUserOwnsStore(any(Store.class));
    assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(id));
  }

  @Test
  public void shouldUpdateProduct() {
    Product product = createTestProduct();
    ProductUpdateDTO updateDTO = new ProductUpdateDTO();
    updateDTO.setName("Updated Product Name");
    updateDTO.setPriceInCents(2000);
    updateDTO.setDescription("Updated description");
    updateDTO.setProductTypeId(savedProductType.getId());

    doNothing().when(authService).assertUserOwnsStore(any(Store.class));

    Product updatedProduct = productService.updateProduct(product.getId(), updateDTO, null);
    assertNotNull(updatedProduct);
    assertEquals("Updated Product Name", updatedProduct.getName());
    assertEquals(2000, updatedProduct.getPriceInCents());
  }

  @Test
  public void shouldThrowException_WhenUpdatingNonExistentProduct() {
    UUID id = UUID.randomUUID();
    ProductUpdateDTO updateDTO = new ProductUpdateDTO();
    updateDTO.setName("Updated Product Name");

    doThrow(new ResourceNotFoundException("Product not found with id: " + id))
        .when(authService)
        .assertUserOwnsStore(any(Store.class));

    assertThrows(
        ResourceNotFoundException.class, () -> productService.updateProduct(id, updateDTO, null));
  }

  @Test
  public void shouldThrowUnauthorizedException_WhenUserIsNotOwner() {
    Product product = createTestProduct();
    doThrow(new UnauthorizedException("User is not the owner of the store"))
        .when(authService)
        .assertUserOwnsStore(any(Store.class));
    assertThrows(UnauthorizedException.class, () -> productService.deleteProduct(product.getId()));
  }

  @Test
  public void shouldThrowException_WhenCreatingProductWithInvalidPrice() {
    ProductCreationDTO invalidDto = new ProductCreationDTO();
    invalidDto.setName("Invalid Product");
    invalidDto.setPriceInCents(0);
    invalidDto.setDescription("This product has invalid price");
    invalidDto.setTypeId(savedProductType.getId());

    doNothing().when(authService).assertUserOwnsStore(any(Store.class));

    assertThrows(
        InvalidRequestException.class,
        () -> productService.createProduct(invalidDto, null, saved_store.getId()));
  }

  @Test
  public void shouldThrowException_WhenCreatingProductWithNegativePrice() {
    ProductCreationDTO invalidDto = new ProductCreationDTO();
    invalidDto.setName("Invalid Product");
    invalidDto.setPriceInCents(-100);
    invalidDto.setDescription("This product has negative price");
    invalidDto.setTypeId(savedProductType.getId());

    doNothing().when(authService).assertUserOwnsStore(any(Store.class));

    assertThrows(
        InvalidRequestException.class,
        () -> productService.createProduct(invalidDto, null, saved_store.getId()));
  }

  @Test
  public void shouldThrowException_WhenUpdatingProductWithInvalidPrice() {
    Product product = createTestProduct();
    ProductUpdateDTO updateDTO = new ProductUpdateDTO();
    updateDTO.setPriceInCents(0);

    doNothing().when(authService).assertUserOwnsStore(any(Store.class));

    assertThrows(
        InvalidRequestException.class,
        () -> productService.updateProduct(product.getId(), updateDTO, null));
  }

  @Test
  public void shouldFindAllProductsByStoreId() {
    createTestProduct();
    List<Product> products = productService.findByStoreId(saved_store.getId());
    assertEquals(1, products.size());
    assertEquals("Test Product", products.get(0).getName());
  }

  @Test
  public void shouldReturnEmptyListWhenNoProductsInStore() {
    UUID anotherStoreId = UUID.randomUUID();
    List<Product> products = productService.findByStoreId(anotherStoreId);
    assertTrue(products.isEmpty());
  }

  @Test
  public void shouldFindAllDiscountedProducts() {
    Product product = createTestProduct();
    doNothing().when(authService).assertUserOwnsStore(any(Store.class));
    productService.updateProductDiscount(product.getId(), 25);

    List<Product> discountedProducts = productService.getAllDiscountedProducts();
    assertNotNull(discountedProducts);
    assertTrue(discountedProducts.stream().allMatch(p -> p.getDiscountPercentage().isPresent()));
  }

  @Test
  public void shouldDeleteProductSuccessfully_WhenNotUsedInPromotionsOrOutfits() {
    Product product = createTestProduct();
    when(outfitProductRepository.existsByProductId(product.getId())).thenReturn(false);
    when(promotionProductRepository.existsByProductId(product.getId())).thenReturn(false);
    doNothing().when(authService).assertUserOwnsStore(any(Store.class));

    productService.deleteProduct(product.getId());

    assertThrows(
        ResourceNotFoundException.class, () -> productService.getProductById(product.getId()));
  }

  @Test
  public void shouldThrowException_WhenDeletingProductUsedInPromotion() {
    Product product = createTestProduct();
    when(outfitProductRepository.existsByProductId(product.getId())).thenReturn(false);
    when(promotionProductRepository.existsByProductId(product.getId())).thenReturn(true);
    doNothing().when(authService).assertUserOwnsStore(any(Store.class));

    assertThrows(
        InvalidRequestException.class, () -> productService.deleteProduct(product.getId()));
  }

  @Test
  public void shouldThrowException_WhenCreatingProductInNonExistentStore() {
    UUID invalidStoreId = UUID.randomUUID();

    assertThrows(
        ResourceNotFoundException.class,
        () -> productService.createProduct(dto, null, invalidStoreId));
  }

  @Test
  public void shouldFindProductsByPromotionId() {
    createTestProduct();
    List<Product> products = productService.findProductsByPromotionId(UUID.randomUUID());
    assertNotNull(products);
  }

  @Test
  public void shouldFindOutfitProductsById() {
    createTestProduct();
    List<Product> products = productService.getOutfitProductsById(UUID.randomUUID());
    assertNotNull(products);
  }

  @Test
  public void shouldPartiallyUpdateProduct() {
    Product product = createTestProduct();
    ProductUpdateDTO updateDTO = new ProductUpdateDTO();
    updateDTO.setName("Updated Name");

    doNothing().when(authService).assertUserOwnsStore(any(Store.class));

    Product updatedProduct = productService.updateProduct(product.getId(), updateDTO, null);
    assertEquals("Updated Name", updatedProduct.getName());
    assertEquals(1000, updatedProduct.getPriceInCents());
  }

  @Test
  public void shouldThrowException_WhenGettingDeletedProduct() {
    Product product = createTestProduct();
    when(outfitProductRepository.existsByProductId(product.getId())).thenReturn(false);
    when(promotionProductRepository.existsByProductId(product.getId())).thenReturn(false);
    doNothing().when(authService).assertUserOwnsStore(any(Store.class));

    productService.deleteProduct(product.getId());

    assertThrows(
        ResourceNotFoundException.class, () -> productService.getProductById(product.getId()));
  }
}
