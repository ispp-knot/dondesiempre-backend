package ispp.project.dondesiempre.controllers;

import ispp.project.dondesiempre.controllers.promotions.PromotionController;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.models.products.ProductType;
import ispp.project.dondesiempre.models.products.dto.ProductCreationDTO;
import ispp.project.dondesiempre.models.promotions.Promotion;
import ispp.project.dondesiempre.models.promotions.dto.PromotionCreationDTO;
import ispp.project.dondesiempre.models.promotions.dto.PromotionDTO;
import ispp.project.dondesiempre.models.promotions.dto.PromotionUpdateDTO;
import ispp.project.dondesiempre.models.storefronts.Storefront;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.repositories.products.ProductTypeRepository;
import ispp.project.dondesiempre.repositories.stores.StoreRepository;
import ispp.project.dondesiempre.services.products.ProductService;
import ispp.project.dondesiempre.services.promotions.PromotionService;
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

  private Storefront createStorefront() {
    Storefront storefront = new Storefront();
    storefront.setIsFirstCollections(true);
    storefront.setPrimaryColor("#c65a3a");
    storefront.setSecondaryColor("#19756a");
    return storefront;
  }

  private Store createAndSaveStore(String name, String email, String storeId) {
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
    return productService.saveProduct(dto);
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
    PromotionCreationDTO promotionCreationDTO = new PromotionCreationDTO();
    promotionCreationDTO.setName("Test Promotion");
    promotionCreationDTO.setDiscountPercentage(20);
    promotionCreationDTO.setActive(true);
    promotionCreationDTO.setProductIds(List.of(product.getId()));
    promotionCreationDTO.setStoreId(store.getId());
    ResponseEntity<?> response = promotionController.createPromotion(promotionCreationDTO);
    assert response.getStatusCode().isSameCodeAs(HttpStatus.OK);
  }

  @Test
  public void shouldGetAllPromotions() {
    ResponseEntity<List<PromotionDTO>> response = promotionController.getAllPromotions();
    assert response.getStatusCode().isSameCodeAs(HttpStatus.OK);
  }

  @Test
  public void shouldGetPromotionById() {
    Store store = createAndSaveStore("Test Store", "test@example.com", "test-store");
    ProductType productType = createAndSaveProductType("Test Product Type");
    Product product = createProduct("Test Product", 1000, 800, productType.getId(), store.getId());
    Promotion promotion =
        createPromotion("Test Promotion", 20, true, List.of(product.getId()), store.getId());

    ResponseEntity<?> response = promotionController.getPromotionById(promotion.getId());
    assert response.getStatusCode().isSameCodeAs(HttpStatus.OK);
  }

  @Test
  public void shouldUpdatePromotionDiscount() {
    Store store = createAndSaveStore("Test Store", "test@example.com", "test-store");
    ProductType productType = createAndSaveProductType("Test Product Type");
    Product product = createProduct("Test Product", 1000, 800, productType.getId(), store.getId());
    Promotion promotion =
        createPromotion("Test Promotion", 20, true, List.of(product.getId()), store.getId());
    PromotionUpdateDTO updatePromotionDTO = new PromotionUpdateDTO();
    Integer newDiscount = 30;
    updatePromotionDTO.setDiscountPercentage(newDiscount);
    ResponseEntity<?> response =
        promotionController.updatePromotion(promotion.getId(), updatePromotionDTO);
    assert response.getStatusCode().isSameCodeAs(HttpStatus.OK);
  }

  @Test
  public void shouldDeletePromotion() {
    Store store = createAndSaveStore("Test Store", "test@example.com", "test-store");
    ProductType productType = createAndSaveProductType("Test Product Type");
    Product product = createProduct("Test Product", 1000, 800, productType.getId(), store.getId());
    Promotion promotion =
        createPromotion("Test Promotion", 20, true, List.of(product.getId()), store.getId());
    ResponseEntity<?> response = promotionController.deletePromotion(promotion.getId());
    assert response.getStatusCode().isSameCodeAs(HttpStatus.NO_CONTENT);
  }
}
