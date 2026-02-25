package ispp.project.dondesiempre.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ispp.project.dondesiempre.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.models.products.ProductType;
import ispp.project.dondesiempre.models.products.dto.ProductCreationDTO;
import ispp.project.dondesiempre.models.promotions.Promotion;
import ispp.project.dondesiempre.models.promotions.dto.PromotionCreationDTO;
import ispp.project.dondesiempre.models.storefronts.Storefront;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.repositories.products.ProductTypeRepository;
import ispp.project.dondesiempre.repositories.stores.StoreRepository;
import ispp.project.dondesiempre.services.products.ProductService;
import ispp.project.dondesiempre.services.promotions.PromotionService;
import java.util.List;
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

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class PromotionServiceTest {

  @Autowired private PromotionService promotionService;
  @Autowired private StoreRepository storeRepository;
  @Autowired private ProductTypeRepository productTypeRepository;
  @Autowired private ProductService productService;

  @Test
  public void shouldCreateNewPromotion() {

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

    PromotionCreationDTO promotionCreationDTO = new PromotionCreationDTO();
    promotionCreationDTO.setName("Test Promotion");
    promotionCreationDTO.setDiscountPercentage(20);
    promotionCreationDTO.setActive(true);
    promotionCreationDTO.setProductIds(List.of(product.getId()));
    Promotion promotion = promotionService.savePromotion(promotionCreationDTO);

    assertNotNull(promotion);
    assertEquals(promotionCreationDTO.getName(), promotion.getName());
    assertEquals(promotionCreationDTO.getDiscountPercentage(), promotion.getDiscountPercentage());
    assertEquals(promotionCreationDTO.isActive(), promotion.isActive());
    assertEquals(
        promotionCreationDTO.getProductIds().size(),
        promotionService.getAllProductsByPromotionId(promotion.getId()).size());
  }

  @Test
  public void shoudlThrowInvalidRequestException_WhenInvalidDiscount() {

    PromotionCreationDTO promotionCreationDTO = new PromotionCreationDTO();
    promotionCreationDTO.setName("Test Promotion");
    promotionCreationDTO.setDiscountPercentage(150); // Invalid discount percentage
    promotionCreationDTO.setActive(true);
    promotionCreationDTO.setProductIds(
        List.of(1, 2, 3)); // Should not reach this check because of invalid discount

    assertThrows(
        InvalidRequestException.class, () -> promotionService.savePromotion(promotionCreationDTO));
  }

  @Test
  public void shoudlThrowResourceNotFoundException_WhenProductDoesNotExist() {

    PromotionCreationDTO promotionCreationDTO = new PromotionCreationDTO();
    promotionCreationDTO.setName("Test Promotion");
    promotionCreationDTO.setDiscountPercentage(20);
    promotionCreationDTO.setActive(true);
    promotionCreationDTO.setProductIds(List.of(99999, 22222)); // Non-existent product IDs

    assertThrows(
        ResourceNotFoundException.class,
        () -> promotionService.savePromotion(promotionCreationDTO));
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

    PromotionCreationDTO promotionCreationDTO = new PromotionCreationDTO();
    promotionCreationDTO.setName("Test Promotion");
    promotionCreationDTO.setDiscountPercentage(20);
    promotionCreationDTO.setActive(true);
    promotionCreationDTO.setProductIds(List.of(product.getId()));
    Promotion promotion = promotionService.savePromotion(promotionCreationDTO);

    Integer expectedId = promotion.getId();
    Promotion foundPromotion = promotionService.getPromotionById(expectedId);
    assertNotNull(foundPromotion);
    assertEquals(expectedId, foundPromotion.getId());
  }

  @Test
  public void shouldThrowResourceNotFoundException_WhenGettingPromotionByNonExistentId() {
    Integer nonExistentId = 99999;
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

    PromotionCreationDTO promotionCreationDTO = new PromotionCreationDTO();
    promotionCreationDTO.setName("Test Promotion");
    promotionCreationDTO.setDiscountPercentage(20);
    promotionCreationDTO.setActive(true);
    promotionCreationDTO.setProductIds(List.of(product.getId()));
    Promotion promotion = promotionService.savePromotion(promotionCreationDTO);
    Integer promotionId = promotion.getId();
    Integer newDiscount = 30;
    Promotion updatedPromotion = promotionService.updatePromotionDiscount(promotionId, newDiscount);
    assertNotNull(updatedPromotion);
    assertEquals(newDiscount, updatedPromotion.getDiscountPercentage());
    assertEquals(promotionId, updatedPromotion.getId());
  }

  @Test
  public void shouldThrowInvalidRequestException_WhenUpdatingPromotionWithInvalidDiscount() {
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

    PromotionCreationDTO promotionCreationDTO = new PromotionCreationDTO();
    promotionCreationDTO.setName("Test Promotion");
    promotionCreationDTO.setDiscountPercentage(20);
    promotionCreationDTO.setActive(true);
    promotionCreationDTO.setProductIds(List.of(product.getId()));
    Promotion promotion = promotionService.savePromotion(promotionCreationDTO);
    Integer promotionId = promotion.getId();
    Integer invalidDiscount = 150; // Invalid discount percentage
    assertThrows(
        InvalidRequestException.class,
        () -> promotionService.updatePromotionDiscount(promotionId, invalidDiscount));
  }

  @Test
  public void shouldDeletePromotion() {
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

    PromotionCreationDTO promotionCreationDTO = new PromotionCreationDTO();
    promotionCreationDTO.setName("Test Promotion");
    promotionCreationDTO.setDiscountPercentage(20);
    promotionCreationDTO.setActive(true);
    promotionCreationDTO.setProductIds(List.of(product.getId()));
    Promotion promotion = promotionService.savePromotion(promotionCreationDTO);

    Integer promotionId = promotion.getId();
    promotionService.deletePromotion(promotionId);
    assertThrows(
        ResourceNotFoundException.class, () -> promotionService.getPromotionById(promotionId));
  }
}
