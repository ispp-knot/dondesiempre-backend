package ispp.project.dondesiempre.controllers;

import ispp.project.dondesiempre.controllers.promotions.PromotionController;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class PromotionControllerTest {

  @Autowired private PromotionController promotionController;
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
    ResponseEntity<?> response = promotionController.createPromotion(promotionCreationDTO);
    assert response.getStatusCode().isSameCodeAs(HttpStatus.OK);
  }

  @Test
  public void shouldGetAllPromotions() {
    ResponseEntity<List<Promotion>> response = promotionController.getAllPromotions();
    assert response.getStatusCode().isSameCodeAs(HttpStatus.OK);
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

    ResponseEntity<?> response = promotionController.getPromotionById(promotion.getId());
    assert response.getStatusCode().isSameCodeAs(HttpStatus.OK);
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

    ResponseEntity<?> response = promotionController.updateDiscount(promotion.getId(), 30);
    assert response.getStatusCode().isSameCodeAs(HttpStatus.OK);
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
    ResponseEntity<?> response = promotionController.deletePromotion(promotion.getId());
    assert response.getStatusCode().isSameCodeAs(HttpStatus.NO_CONTENT);
  }
}
