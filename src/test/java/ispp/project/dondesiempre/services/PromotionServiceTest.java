package ispp.project.dondesiempre.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.auth.repositories.UserRepository;
import ispp.project.dondesiempre.modules.auth.services.AuthService;
import ispp.project.dondesiempre.modules.common.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.common.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.modules.products.dtos.ProductCreationDTO;
import ispp.project.dondesiempre.modules.products.models.Product;
import ispp.project.dondesiempre.modules.products.models.ProductType;
import ispp.project.dondesiempre.modules.products.repositories.ProductTypeRepository;
import ispp.project.dondesiempre.modules.products.services.ProductService;
import ispp.project.dondesiempre.modules.promotions.dtos.PromotionCreationDTO;
import ispp.project.dondesiempre.modules.promotions.dtos.PromotionUpdateDTO;
import ispp.project.dondesiempre.modules.promotions.models.Promotion;
import ispp.project.dondesiempre.modules.promotions.models.PromotionProduct;
import ispp.project.dondesiempre.modules.promotions.services.PromotionService;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.models.Storefront;
import ispp.project.dondesiempre.modules.stores.repositories.StoreRepository;
import java.util.List;
import java.util.UUID;
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
public class PromotionServiceTest {

  @Autowired private PromotionService promotionService;
  @Autowired private StoreRepository storeRepository;
  @Autowired private ProductTypeRepository productTypeRepository;
  @Autowired private ProductService productService;
  @Autowired private UserRepository userRepository;
  @MockitoBean private AuthService authService;

  private Storefront createStorefront() {
    Storefront storefront = new Storefront();
    storefront.setIsFirstCollections(true);
    storefront.setPrimaryColor("#c65a3a");
    storefront.setSecondaryColor("#19756a");
    return storefront;
  }

  private Store createAndSaveStore(String name, String email, String storeId) {

    User user = new User();
    user.setEmail("user@example.com");
    user.setPassword("password");
    user = userRepository.save(user);

    Store store = new Store();
    store.setName(name);
    store.setEmail(email);
    store.setStoreID(storeId);
    store.setLocation(
        new Point(
            new CoordinateArraySequence(new Coordinate[] {new Coordinate(0.0, 0.0)}),
            new GeometryFactory(new PrecisionModel(PrecisionModel.FIXED), 0)));
    store.setAddress("123 Test Street");
    store.setOpeningHours("9am - 5pm");
    store.setAcceptsShipping(true);
    store.setStorefront(createStorefront());
    store.setUser(user);
    return storeRepository.save(store);
  }

  private ProductType createAndSaveProductType(String type) {
    ProductType productType = new ProductType();
    productType.setType(type);
    return productTypeRepository.save(productType);
  }

  private Product createProduct(
      String name,
      Integer priceInCents,
      Integer discountedPriceInCents,
      UUID typeId,
      UUID storeId) {
    ProductCreationDTO dto = new ProductCreationDTO();
    dto.setName(name);
    dto.setPriceInCents(priceInCents);
    dto.setDiscountedPriceInCents(discountedPriceInCents);
    dto.setDescription("This is a test product");
    dto.setTypeId(typeId);
    dto.setStoreId(storeId);
    return productService.saveProduct(dto, null);
  }

  private Promotion createPromotion(
      String name, Integer discount, Boolean active, List<UUID> productIds, UUID storeId) {
    PromotionCreationDTO promotionCreationDTO = new PromotionCreationDTO();
    promotionCreationDTO.setName(name);
    promotionCreationDTO.setDiscountPercentage(discount);
    promotionCreationDTO.setActive(active);
    promotionCreationDTO.setProductIds(productIds);
    promotionCreationDTO.setStoreId(storeId);
    return promotionService.savePromotion(promotionCreationDTO);
  }

  @Test
  public void shouldCreateNewPromotion() {
    Store store = createAndSaveStore("Test Store", "test@example.com", "test-store");
    ProductType productType = createAndSaveProductType("Test Product Type");
    Product product = createProduct("Test Product", 1000, 800, productType.getId(), store.getId());
    Promotion promotion =
        createPromotion("Test Promotion", 20, true, List.of(product.getId()), store.getId());

    assertNotNull(promotion);
    assertEquals("Test Promotion", promotion.getName());
    assertEquals(20, promotion.getDiscountPercentage());
    assertEquals(true, promotion.isActive());
    assertEquals(1, promotionService.getAllProductsByPromotionId(promotion.getId()).size());
  }

  @Test
  public void shoudlThrowInvalidRequestException_WhenInvalidDiscount() {

    PromotionCreationDTO promotionCreationDTO = new PromotionCreationDTO();
    promotionCreationDTO.setName("Test Promotion");
    promotionCreationDTO.setDiscountPercentage(150); // Invalid discount percentage
    promotionCreationDTO.setActive(true);
    promotionCreationDTO.setProductIds(
        List.of(
            UUID.randomUUID(),
            UUID.randomUUID())); // Should not reach this check because of invalid discount

    assertThrows(
        InvalidRequestException.class, () -> promotionService.savePromotion(promotionCreationDTO));
  }

  @Test
  public void shoudlThrowResourceNotFoundException_WhenProductDoesNotExist() {
    Store store = createAndSaveStore("Test Store", "test@example.com", "test-store");

    PromotionCreationDTO promotionCreationDTO = new PromotionCreationDTO();
    promotionCreationDTO.setName("Test Promotion");
    promotionCreationDTO.setDiscountPercentage(20);
    promotionCreationDTO.setActive(true);
    promotionCreationDTO.setStoreId(store.getId());
    promotionCreationDTO.setProductIds(
        List.of(UUID.randomUUID(), UUID.randomUUID())); // Non-existent product IDs

    assertThrows(
        ResourceNotFoundException.class,
        () -> promotionService.savePromotion(promotionCreationDTO));
  }

  @Test
  public void shoudlThrowInvalidRequestException_WhenProductsAreNotFromTheSameStore() {
    Store store = createAndSaveStore("Test Store", "test@example.com", "test-store");
    Store anotherStore = createAndSaveStore("Another Store", "test@another.com", "another-store");
    ProductType productType = createAndSaveProductType("Test Product Type");

    Product product = createProduct("Test Product", 1000, 800, productType.getId(), store.getId());
    Product anotherProduct =
        createProduct("Another-product", 1000, 800, productType.getId(), anotherStore.getId());

    PromotionCreationDTO promotionCreationDTO = new PromotionCreationDTO();
    promotionCreationDTO.setName("Test Promotion");
    promotionCreationDTO.setDiscountPercentage(20);
    promotionCreationDTO.setActive(true);
    promotionCreationDTO.setStoreId(store.getId());
    promotionCreationDTO.setProductIds(List.of(product.getId(), anotherProduct.getId()));

    assertThrows(
        InvalidRequestException.class, () -> promotionService.savePromotion(promotionCreationDTO));
  }

  @Test
  public void shouldCalculateDiscountedPrice() {
    Integer originalPrice = 1000; // $10.00
    Integer discountPercentage = 25; // 25% discount
    Integer expectedPrice = 750; // $7.50 after discount
    Integer discountedPrice =
        promotionService.calculateDiscountedPrice(originalPrice, discountPercentage);
    assertEquals(expectedPrice, discountedPrice);
  }

  @Test
  public void
      shouldThrowInvalidRequestException_WhenCalculatingDiscountedPriceWithInvalidDiscount() {
    Integer originalPrice = 1000; // $10.00
    Integer invalidDiscountPercentage = 150; // Invalid discount percentage
    assertThrows(
        InvalidRequestException.class,
        () -> promotionService.calculateDiscountedPrice(originalPrice, invalidDiscountPercentage));
  }

  @Test
  public void shouldThrowInvalidRequestException_WhenCalculatingDiscountedPriceWithNullValues() {
    Integer originalPrice = null;
    Integer discountPercentage = 20;
    assertThrows(
        InvalidRequestException.class,
        () -> promotionService.calculateDiscountedPrice(originalPrice, discountPercentage));
  }

  @Test
  public void shouldGetPromotionById() {
    Store store = createAndSaveStore("Test Store", "test@example.com", "test-store");
    ProductType productType = createAndSaveProductType("Test Product Type");
    Product product = createProduct("Test Product", 1000, 800, productType.getId(), store.getId());
    Promotion promotion =
        createPromotion("Test Promotion", 20, true, List.of(product.getId()), store.getId());

    UUID expectedId = promotion.getId();
    Promotion foundPromotion = promotionService.getPromotionById(expectedId);
    assertNotNull(foundPromotion);
    assertEquals(expectedId, foundPromotion.getId());
  }

  @Test
  public void shouldThrowResourceNotFoundException_WhenGettingPromotionByNonExistentId() {
    UUID nonExistentId = UUID.randomUUID();
    assertThrows(
        ResourceNotFoundException.class, () -> promotionService.getPromotionById(nonExistentId));
  }

  @Test
  public void shouldGetAllPromotions() {
    List<Promotion> promotions = promotionService.getAllPromotions();
    assertNotNull(promotions);
  }

  @Test
  public void shouldUpdatePromotionDiscount() {
    Store store = createAndSaveStore("Test Store", "test@example.com", "test-store");
    ProductType productType = createAndSaveProductType("Test Product Type");
    Product product = createProduct("Test Product", 1000, 800, productType.getId(), store.getId());
    Promotion promotion =
        createPromotion("Test Promotion", 20, true, List.of(product.getId()), store.getId());
    UUID promotionId = promotion.getId();
    PromotionUpdateDTO updateDTO = new PromotionUpdateDTO();
    Integer newDiscount = 30;
    updateDTO.setDiscountPercentage(newDiscount);
    Promotion updatedPromotion = promotionService.updatePromotion(promotionId, updateDTO);
    assertNotNull(updatedPromotion);
    assertEquals(newDiscount, updatedPromotion.getDiscountPercentage());
    assertEquals(promotionId, updatedPromotion.getId());
  }

  @Test
  public void shouldThrowInvalidRequestException_WhenUpdatingPromotionWithInvalidDiscount() {
    Store store = createAndSaveStore("Test Store", "test@example.com", "test-store");
    ProductType productType = createAndSaveProductType("Test Product Type");
    Product product = createProduct("Test Product", 1000, 800, productType.getId(), store.getId());
    Promotion promotion =
        createPromotion("Test Promotion", 20, true, List.of(product.getId()), store.getId());
    UUID promotionId = promotion.getId();

    PromotionUpdateDTO updateDTO = new PromotionUpdateDTO();
    Integer invalidDiscount = 150; // Invalid discount percentage
    updateDTO.setDiscountPercentage(invalidDiscount);
    assertThrows(
        InvalidRequestException.class,
        () -> promotionService.updatePromotion(promotionId, updateDTO));
  }

  @Test
  public void shouldDeletePromotion() {
    Store store = createAndSaveStore("Test Store", "test@example.com", "test-store");
    ProductType productType = createAndSaveProductType("Test Product Type");
    Product product = createProduct("Test Product", 1000, 800, productType.getId(), store.getId());
    Promotion promotion =
        createPromotion("Test Promotion", 20, true, List.of(product.getId()), store.getId());

    UUID promotionId = promotion.getId();
    promotionService.deletePromotion(promotionId);
    assertThrows(
        ResourceNotFoundException.class, () -> promotionService.getPromotionById(promotionId));
    assertDoesNotThrow(() -> productService.getProductById(product.getId()));
  }

  @Test
  public void shouldAddProductToPromotion() {
    Store store = createAndSaveStore("Test Store", "test@test.com", "test-store");
    ProductType productType = createAndSaveProductType("Test Product Type");
    Product product = createProduct("Test Product", 1000, 800, productType.getId(), store.getId());
    Product product2 =
        createProduct("Test Product 2", 1000, 800, productType.getId(), store.getId());
    Promotion promotion =
        createPromotion("Test Promotion", 20, true, List.of(product.getId()), store.getId());
    UUID promotionId = promotion.getId();
    PromotionProduct promotionProduct = promotionService.addProduct(promotionId, product2.getId());

    assertNotNull(promotionProduct);
    assertEquals(promotionId, promotionProduct.getPromotion().getId());
    assertEquals(product2.getId(), promotionProduct.getProduct().getId());
  }

  @Test
  public void shouldThrowResourceNotFoundException_WhenAddingNonExistentProductToPromotion() {
    Store store = createAndSaveStore("Test Store", "test@test.com", "test-store");
    ProductType productType = createAndSaveProductType("Test Product Type");
    Product product = createProduct("Test Product", 1000, 800, productType.getId(), store.getId());
    Promotion promotion =
        createPromotion("Test Promotion", 20, true, List.of(product.getId()), store.getId());
    UUID promotionId = promotion.getId();

    assertThrows(
        ResourceNotFoundException.class,
        () -> promotionService.addProduct(promotionId, UUID.randomUUID()));
  }

  @Test
  public void shouldThrowUnauthorizedException_WhenCreatingPromotionWithoutAuthorization() {
    doThrow(new UnauthorizedException("Not authorized"))
        .when(authService)
        .assertUserOwnsStore(any());

    Store store = createAndSaveStore("Test Store", "test@test.com", "test-store");
    PromotionCreationDTO promotionCreationDTO = new PromotionCreationDTO();
    promotionCreationDTO.setName("Test Promotion");
    promotionCreationDTO.setDiscountPercentage(20);
    promotionCreationDTO.setActive(true);
    promotionCreationDTO.setProductIds(List.of(UUID.randomUUID()));
    promotionCreationDTO.setStoreId(store.getId());

    assertThrows(
        UnauthorizedException.class, () -> promotionService.savePromotion(promotionCreationDTO));
  }

  @Test
  public void shouldThrowUnauthorizedException_WhenUpdatingPromotionWithoutAuthorization() {
    Store store = createAndSaveStore("Test Store", "test@example.com", "test-store");
    ProductType productType = createAndSaveProductType("Test Product Type");
    Product product = createProduct("Test Product", 1000, 800, productType.getId(), store.getId());
    Promotion promotion =
        createPromotion("Test Promotion", 20, true, List.of(product.getId()), store.getId());

    doThrow(new UnauthorizedException("Not authorized"))
        .when(authService)
        .assertUserOwnsStore(any());

    PromotionUpdateDTO updateDTO = new PromotionUpdateDTO();
    updateDTO.setDiscountPercentage(30);

    assertThrows(
        UnauthorizedException.class,
        () -> promotionService.updatePromotion(promotion.getId(), updateDTO));
  }

  @Test
  public void shouldThrowUnauthorizedException_WhenDeletingPromotionWithoutAuthorization() {
    Store store = createAndSaveStore("Test Store", "test@example.com", "test-store");
    ProductType productType = createAndSaveProductType("Test Product Type");
    Product product = createProduct("Test Product", 1000, 800, productType.getId(), store.getId());
    Promotion promotion =
        createPromotion("Test Promotion", 20, true, List.of(product.getId()), store.getId());

    doThrow(new UnauthorizedException("Not authorized"))
        .when(authService)
        .assertUserOwnsStore(any());

    assertThrows(
        UnauthorizedException.class, () -> promotionService.deletePromotion(promotion.getId()));
  }

  @Test
  public void shouldThrowUnauthorizedException_WhenAddingProductToPromotionWithoutAuthorization() {
    Store store = createAndSaveStore("Test Store", "test@test.com", "test-store");
    ProductType productType = createAndSaveProductType("Test Product Type");
    Product product = createProduct("Test Product", 1000, 800, productType.getId(), store.getId());
    Promotion promotion =
        createPromotion("Test Promotion", 20, true, List.of(product.getId()), store.getId());

    Product product2 =
        createProduct("Test Product 2", 1000, 800, productType.getId(), store.getId());

    doThrow(new UnauthorizedException("Not authorized"))
        .when(authService)
        .assertUserOwnsStore(any());

    assertThrows(
        UnauthorizedException.class,
        () -> promotionService.addProduct(promotion.getId(), product2.getId()));
  }
}
