package ispp.project.dondesiempre.modules.products.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.auth.repositories.UserRepository;
import ispp.project.dondesiempre.modules.auth.services.AuthService;
import ispp.project.dondesiempre.modules.common.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.common.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.modules.products.dtos.ProductVariantCreationDTO;
import ispp.project.dondesiempre.modules.products.dtos.ProductVariantUpdateDTO;
import ispp.project.dondesiempre.modules.products.models.Product;
import ispp.project.dondesiempre.modules.products.models.ProductColor;
import ispp.project.dondesiempre.modules.products.models.ProductSize;
import ispp.project.dondesiempre.modules.products.models.ProductType;
import ispp.project.dondesiempre.modules.products.models.ProductVariant;
import ispp.project.dondesiempre.modules.products.repositories.ProductColorRepository;
import ispp.project.dondesiempre.modules.products.repositories.ProductRepository;
import ispp.project.dondesiempre.modules.products.repositories.ProductSizeRepository;
import ispp.project.dondesiempre.modules.products.repositories.ProductTypeRepository;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.models.Storefront;
import ispp.project.dondesiempre.modules.stores.repositories.StoreRepository;
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
public class ProductVariantServiceTest {

  @Autowired private ProductVariantService productVariantService;
  @Autowired private ProductRepository productRepository;
  @Autowired private ProductTypeRepository productTypeRepository;
  @Autowired private ProductColorRepository productColorRepository;
  @Autowired private ProductSizeRepository productSizeRepository;
  @Autowired private StoreRepository storeRepository;
  @Autowired private UserRepository userRepository;
  @MockitoBean private AuthService authService;
  @Autowired private CoordinatesService coordinatesService;

  private User testUser;
  private Store savedStore;
  private Product savedProduct;
  private ProductSize savedSize;
  private ProductColor savedColor;
  private ProductVariantCreationDTO dto;

  @BeforeEach
  void setUp() {
    User user = new User();
    user.setEmail("test-variant@test.com");
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
    savedStore = storeRepository.save(store);

    ProductType type = new ProductType();
    type.setType("Test Product Type");
    type = productTypeRepository.save(type);

    Product product = new Product();
    product.setName("Test Product");
    product.setPriceInCents(1000);
    product.setDescription("This is a test product");
    product.setType(type);
    product.setStore(savedStore);
    savedProduct = productRepository.save(product);

    ProductSize size = new ProductSize();
    size.setSize("M");
    savedSize = productSizeRepository.save(size);

    ProductColor color = new ProductColor();
    color.setColor("Red");
    savedColor = productColorRepository.save(color);

    dto = new ProductVariantCreationDTO();
    dto.setProductId(savedProduct.getId());
    dto.setSizeId(savedSize.getId());
    dto.setColorId(savedColor.getId());
    dto.setIsAvailable(true);
  }

  @Test
  public void shouldCreateNewProductVariant() {
    doNothing().when(authService).assertUserOwnsStore(any(Store.class));

    ProductVariant variant = productVariantService.createProductVariant(dto);

    assertNotNull(variant);
    assertNotNull(variant.getId());
    assertEquals(savedProduct.getId(), variant.getProduct().getId());
    assertEquals(savedSize.getId(), variant.getSize().getId());
    assertEquals(savedColor.getId(), variant.getColor().getId());
    assertTrue(variant.getIsAvailable());
  }

  @Test
  public void shouldThrowException_WhenProductNotFound() {
    doNothing().when(authService).assertUserOwnsStore(any(Store.class));
    dto.setProductId(UUID.randomUUID());

    assertThrows(
        ResourceNotFoundException.class, () -> productVariantService.createProductVariant(dto));
  }

  @Test
  public void shouldThrowException_WhenSizeNotFound() {
    doNothing().when(authService).assertUserOwnsStore(any(Store.class));
    dto.setSizeId(UUID.randomUUID());

    assertThrows(
        ResourceNotFoundException.class, () -> productVariantService.createProductVariant(dto));
  }

  @Test
  public void shouldThrowException_WhenColorNotFound() {
    doNothing().when(authService).assertUserOwnsStore(any(Store.class));
    dto.setColorId(UUID.randomUUID());

    assertThrows(
        ResourceNotFoundException.class, () -> productVariantService.createProductVariant(dto));
  }

  @Test
  public void shouldThrowUnauthorizedException_WhenUserIsNotOwner() {
    doThrow(new UnauthorizedException("User is not the owner of the store"))
        .when(authService)
        .assertUserOwnsStore(any(Store.class));

    assertThrows(
        UnauthorizedException.class, () -> productVariantService.createProductVariant(dto));
  }

  @Test
  public void shouldGetProductVariantById() {
    doNothing().when(authService).assertUserOwnsStore(any(Store.class));

    ProductVariant createdVariant = productVariantService.createProductVariant(dto);
    ProductVariant fetchedVariant =
        productVariantService.getProductVariantById(createdVariant.getId());

    assertNotNull(fetchedVariant);
    assertEquals(createdVariant.getId(), fetchedVariant.getId());
    assertEquals(createdVariant.getProduct().getId(), fetchedVariant.getProduct().getId());
  }

  @Test
  public void shouldThrowException_WhenGettingNonExistentVariant() {
    UUID id = UUID.randomUUID();

    assertThrows(
        ResourceNotFoundException.class, () -> productVariantService.getProductVariantById(id));
  }

  @Test
  public void shouldGetAllProductVariants() {
    doNothing().when(authService).assertUserOwnsStore(any(Store.class));

    productVariantService.createProductVariant(dto);
    List<ProductVariant> variants = productVariantService.getAllProductVariants();

    assertNotNull(variants);
    assertFalse(variants.isEmpty());
  }

  @Test
  public void shouldGetVariantsByProductId() {
    doNothing().when(authService).assertUserOwnsStore(any(Store.class));

    productVariantService.createProductVariant(dto);
    List<ProductVariant> variants =
        productVariantService.getVariantsByProductId(savedProduct.getId());

    assertNotNull(variants);
    assertFalse(variants.isEmpty());
    assertTrue(
        variants.stream().allMatch(v -> v.getProduct().getId().equals(savedProduct.getId())));
  }

  @Test
  public void shouldGetAvailableVariantsByProductId() {
    doNothing().when(authService).assertUserOwnsStore(any(Store.class));

    productVariantService.createProductVariant(dto);

    ProductSize size2 = new ProductSize();
    size2.setSize("L");
    size2 = productSizeRepository.save(size2);

    ProductVariantCreationDTO unavailableDto = new ProductVariantCreationDTO();
    unavailableDto.setProductId(savedProduct.getId());
    unavailableDto.setSizeId(size2.getId());
    unavailableDto.setColorId(savedColor.getId());
    unavailableDto.setIsAvailable(false);

    productVariantService.createProductVariant(unavailableDto);

    List<ProductVariant> variants =
        productVariantService.getAvailableVariantsByProductId(savedProduct.getId());

    assertNotNull(variants);
    assertEquals(1, variants.size());
    assertTrue(variants.stream().allMatch(ProductVariant::getIsAvailable));
  }

  @Test
  public void shouldUpdateProductVariant() {
    doNothing().when(authService).assertUserOwnsStore(any(Store.class));

    ProductVariant createdVariant = productVariantService.createProductVariant(dto);
    assertTrue(createdVariant.getIsAvailable());

    ProductVariantUpdateDTO updateDto = new ProductVariantUpdateDTO();
    updateDto.setIsAvailable(false);

    ProductVariant updatedVariant =
        productVariantService.updateProductVariant(createdVariant.getId(), updateDto);

    assertNotNull(updatedVariant);
    assertFalse(updatedVariant.getIsAvailable());
  }

  @Test
  public void shouldThrowException_WhenUpdatingNonExistentVariant() {
    doNothing().when(authService).assertUserOwnsStore(any(Store.class));
    UUID id = UUID.randomUUID();
    ProductVariantUpdateDTO updateDto = new ProductVariantUpdateDTO();
    updateDto.setIsAvailable(false);

    assertThrows(
        ResourceNotFoundException.class,
        () -> productVariantService.updateProductVariant(id, updateDto));
  }

  @Test
  public void shouldThrowUnauthorizedException_WhenUpdatingUnauthorized() {
    doNothing().when(authService).assertUserOwnsStore(any(Store.class));
    ProductVariant createdVariant = productVariantService.createProductVariant(dto);

    doThrow(new UnauthorizedException("User is not the owner of the store"))
        .when(authService)
        .assertUserOwnsStore(any(Store.class));

    ProductVariantUpdateDTO updateDto = new ProductVariantUpdateDTO();
    updateDto.setIsAvailable(false);

    assertThrows(
        UnauthorizedException.class,
        () -> productVariantService.updateProductVariant(createdVariant.getId(), updateDto));
  }

  @Test
  public void shouldDeleteProductVariant() {
    doNothing().when(authService).assertUserOwnsStore(any(Store.class));

    ProductVariant createdVariant = productVariantService.createProductVariant(dto);
    UUID variantId = createdVariant.getId();

    productVariantService.deleteProductVariant(variantId);

    assertThrows(
        ResourceNotFoundException.class,
        () -> productVariantService.getProductVariantById(variantId));
  }

  @Test
  public void shouldThrowException_WhenDeletingNonExistentVariant() {
    UUID id = UUID.randomUUID();

    assertThrows(
        ResourceNotFoundException.class, () -> productVariantService.deleteProductVariant(id));
  }

  @Test
  public void shouldThrowUnauthorizedException_WhenDeletingUnauthorized() {
    doNothing().when(authService).assertUserOwnsStore(any(Store.class));
    ProductVariant createdVariant = productVariantService.createProductVariant(dto);

    doThrow(new UnauthorizedException("User is not the owner of the store"))
        .when(authService)
        .assertUserOwnsStore(any(Store.class));

    assertThrows(
        UnauthorizedException.class,
        () -> productVariantService.deleteProductVariant(createdVariant.getId()));
  }

  @Test
  public void shouldThrowInvalidRequestException_WhenCreatingDuplicateVariant() {
    doNothing().when(authService).assertUserOwnsStore(any(Store.class));

    productVariantService.createProductVariant(dto);

    assertThrows(
        InvalidRequestException.class, () -> productVariantService.createProductVariant(dto));
  }

  @Test
  public void shouldCreateMultipleDifferentVariantsForSameProduct() {
    doNothing().when(authService).assertUserOwnsStore(any(Store.class));

    productVariantService.createProductVariant(dto);

    ProductSize size2 = new ProductSize();
    size2.setSize("L");
    size2 = productSizeRepository.save(size2);

    ProductVariantCreationDTO dto2 = new ProductVariantCreationDTO();
    dto2.setProductId(savedProduct.getId());
    dto2.setSizeId(size2.getId());
    dto2.setColorId(savedColor.getId());
    dto2.setIsAvailable(true);

    productVariantService.createProductVariant(dto2);

    assertEquals(2, productVariantService.getVariantsByProductId(savedProduct.getId()).size());
  }

  @Test
  public void shouldFetchVariantWithAllRelations() {
    doNothing().when(authService).assertUserOwnsStore(any(Store.class));

    ProductVariant createdVariant = productVariantService.createProductVariant(dto);
    ProductVariant fetchedVariant =
        productVariantService.getProductVariantById(createdVariant.getId());

    assertNotNull(fetchedVariant.getProduct());
    assertNotNull(fetchedVariant.getSize());
    assertNotNull(fetchedVariant.getColor());
    assertEquals(savedProduct.getId(), fetchedVariant.getProduct().getId());
    assertEquals(savedSize.getId(), fetchedVariant.getSize().getId());
    assertEquals(savedColor.getId(), fetchedVariant.getColor().getId());
  }
}
