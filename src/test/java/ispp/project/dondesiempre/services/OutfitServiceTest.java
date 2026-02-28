package ispp.project.dondesiempre.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import ispp.project.dondesiempre.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.models.User;
import ispp.project.dondesiempre.models.outfits.Outfit;
import ispp.project.dondesiempre.models.outfits.dto.OutfitCreationDTO;
import ispp.project.dondesiempre.models.outfits.dto.OutfitCreationProductDTO;
import ispp.project.dondesiempre.models.outfits.dto.OutfitUpdateDTO;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.models.products.ProductType;
import ispp.project.dondesiempre.models.storefronts.Storefront;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.repositories.UserRepository;
import ispp.project.dondesiempre.repositories.products.ProductRepository;
import ispp.project.dondesiempre.repositories.products.ProductTypeRepository;
import ispp.project.dondesiempre.repositories.storefronts.StorefrontRepository;
import ispp.project.dondesiempre.repositories.stores.StoreRepository;
import ispp.project.dondesiempre.services.outfits.OutfitService;
import java.util.List;
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
public class OutfitServiceTest {

  @Autowired private OutfitService outfitService;
  @Autowired private UserRepository userRepository;
  @Autowired private StorefrontRepository storefrontRepository;
  @Autowired private StoreRepository storeRepository;
  @Autowired private ProductTypeRepository productTypeRepository;
  @Autowired private ProductRepository productRepository;
  @MockitoBean private UserService userService;

  private Storefront storefront;
  private Store store;
  private Product product;

  @BeforeEach
  void setUp() {
    User user = new User();
    user.setEmail("outfit-service-test@test.com");
    user.setPassword("password");
    user = userRepository.save(user);

    storefront = new Storefront();
    storefront = storefrontRepository.save(storefront);

    store = new Store();
    store.setName("Test Store");
    store.setEmail("store@test.com");
    store.setStoreID("outfit-test-store");
    store.setAddress("Test address");
    store.setOpeningHours("9-5");
    store.setAcceptsShipping(false);
    store.setLocation(
        new Point(
            new CoordinateArraySequence(new Coordinate[] {new Coordinate(0.0, 0.0)}),
            new GeometryFactory(new PrecisionModel(PrecisionModel.FIXED), 0)));
    store.setStorefront(storefront);
    store.setUser(user);
    store = storeRepository.save(store);

    ProductType type = new ProductType();
    type.setType("Test Type");
    type = productTypeRepository.save(type);

    product = new Product();
    product.setName("Test Product");
    product.setPriceInCents(1000);
    product.setDiscountedPriceInCents(800);
    product.setType(type);
    product.setStore(store);
    product = productRepository.save(product);
  }

  private OutfitCreationDTO buildCreationDTO() {
    OutfitCreationProductDTO productDTO = new OutfitCreationProductDTO();
    productDTO.setId(product.getId());
    productDTO.setIndex(0);

    OutfitCreationDTO dto = new OutfitCreationDTO();
    dto.setName("Test Outfit");
    dto.setDescription("Test description");
    dto.setIndex(0);
    dto.setStorefrontId(storefront.getId());
    dto.setTags(List.of());
    dto.setProducts(List.of(productDTO));
    return dto;
  }

  @Test
  void create_shouldCreateOutfit_whenAuthorized() {
    Outfit result = outfitService.create(buildCreationDTO());

    assertNotNull(result);
    assertEquals("Test Outfit", result.getName());
  }

  @Test
  void create_shouldThrowUnauthorizedException_whenNotAuthorized() {
    doThrow(new UnauthorizedException("Not authorized"))
        .when(userService)
        .assertUserOwnsStore(any());

    assertThrows(UnauthorizedException.class, () -> outfitService.create(buildCreationDTO()));
  }

  @Test
  void update_shouldUpdateOutfit_whenAuthorized() {
    Outfit created = outfitService.create(buildCreationDTO());

    OutfitUpdateDTO updateDTO = new OutfitUpdateDTO();
    updateDTO.setName("Updated Outfit");
    updateDTO.setDescription("Updated description");
    updateDTO.setIndex(1);
    updateDTO.setDiscountedPriceInCents(500);

    Outfit result = outfitService.update(created.getId(), updateDTO);

    assertEquals("Updated Outfit", result.getName());
    assertEquals(1, result.getIndex());
  }

  @Test
  void update_shouldThrowUnauthorizedException_whenNotAuthorized() {
    Outfit created = outfitService.create(buildCreationDTO());

    doThrow(new UnauthorizedException("Not authorized"))
        .when(userService)
        .assertUserOwnsStore(any());

    OutfitUpdateDTO updateDTO = new OutfitUpdateDTO();
    updateDTO.setName("Updated Outfit");
    updateDTO.setDiscountedPriceInCents(500);
    updateDTO.setIndex(0);

    assertThrows(
        UnauthorizedException.class, () -> outfitService.update(created.getId(), updateDTO));
  }

  @Test
  void delete_shouldDeleteOutfit_whenAuthorized() {
    Outfit created = outfitService.create(buildCreationDTO());
    UUID outfitId = created.getId();

    assertDoesNotThrow(() -> outfitService.delete(outfitId));
  }

  @Test
  void delete_shouldThrowUnauthorizedException_whenNotAuthorized() {
    Outfit created = outfitService.create(buildCreationDTO());
    UUID outfitId = created.getId();

    doThrow(new UnauthorizedException("Not authorized"))
        .when(userService)
        .assertUserOwnsStore(any());

    assertThrows(UnauthorizedException.class, () -> outfitService.delete(outfitId));
  }

  @Test
  void addTag_shouldAddTag_whenAuthorized() {
    Outfit created = outfitService.create(buildCreationDTO());

    String tagName = outfitService.addTag(created.getId(), "cool");

    assertEquals("cool", tagName);
  }

  @Test
  void addTag_shouldThrowUnauthorizedException_whenNotAuthorized() {
    Outfit created = outfitService.create(buildCreationDTO());

    doThrow(new UnauthorizedException("Not authorized"))
        .when(userService)
        .assertUserOwnsStore(any());

    assertThrows(UnauthorizedException.class, () -> outfitService.addTag(created.getId(), "cool"));
  }

  @Test
  void addProduct_shouldAddProduct_whenAuthorized() {
    Outfit created = outfitService.create(buildCreationDTO());

    Product extra = new Product();
    extra.setName("Extra Product");
    extra.setPriceInCents(500);
    extra.setDiscountedPriceInCents(500);
    extra.setType(product.getType());
    extra.setStore(store);
    extra = productRepository.save(extra);

    OutfitCreationProductDTO extraDTO = new OutfitCreationProductDTO();
    extraDTO.setId(extra.getId());
    extraDTO.setIndex(1);

    assertDoesNotThrow(() -> outfitService.addProduct(created.getId(), extraDTO));
  }

  @Test
  void addProduct_shouldThrowUnauthorizedException_whenNotAuthorized() {
    Outfit created = outfitService.create(buildCreationDTO());

    doThrow(new UnauthorizedException("Not authorized"))
        .when(userService)
        .assertUserOwnsStore(any());

    OutfitCreationProductDTO productDTO = new OutfitCreationProductDTO();
    productDTO.setId(product.getId());
    productDTO.setIndex(1);

    assertThrows(
        UnauthorizedException.class, () -> outfitService.addProduct(created.getId(), productDTO));
  }

  @Test
  void findById_shouldReturnOutfit_whenExists() {
    Outfit created = outfitService.create(buildCreationDTO());
    Outfit found = outfitService.findById(created.getId());

    assertNotNull(found);
    assertEquals(created.getId(), found.getId());
  }

  @Test
  void findById_shouldThrowResourceNotFoundException_whenNotFound() {
    assertThrows(ResourceNotFoundException.class, () -> outfitService.findById(UUID.randomUUID()));
  }

  @Test
  void create_shouldThrowInvalidRequestException_whenNoProducts() {
    OutfitCreationDTO dto = new OutfitCreationDTO();
    dto.setName("Empty Outfit");
    dto.setIndex(0);
    dto.setStorefrontId(storefront.getId());
    dto.setTags(List.of());
    dto.setProducts(List.of());

    assertThrows(InvalidRequestException.class, () -> outfitService.create(dto));
  }

  @Test
  void removeTag_shouldRemoveTag_whenAuthorized() {
    Outfit created = outfitService.create(buildCreationDTO());
    outfitService.addTag(created.getId(), "cool");

    assertDoesNotThrow(() -> outfitService.removeTag(created.getId(), "cool"));
  }

  @Test
  void removeTag_shouldThrowUnauthorizedException_whenNotAuthorized() {
    Outfit created = outfitService.create(buildCreationDTO());
    outfitService.addTag(created.getId(), "cool");

    doThrow(new UnauthorizedException("Not authorized"))
        .when(userService)
        .assertUserOwnsStore(any());

    assertThrows(
        UnauthorizedException.class, () -> outfitService.removeTag(created.getId(), "cool"));
  }

  @Test
  void removeProduct_shouldRemoveProduct_whenAuthorized() {
    Outfit created = outfitService.create(buildCreationDTO());

    assertDoesNotThrow(() -> outfitService.removeProduct(created.getId(), product));
  }

  @Test
  void removeProduct_shouldThrowUnauthorizedException_whenNotAuthorized() {
    Outfit created = outfitService.create(buildCreationDTO());

    doThrow(new UnauthorizedException("Not authorized"))
        .when(userService)
        .assertUserOwnsStore(any());

    assertThrows(
        UnauthorizedException.class, () -> outfitService.removeProduct(created.getId(), product));
  }

  @Test
  void sortProducts_shouldSortProducts_whenAuthorized() {
    Product extra = new Product();
    extra.setName("Extra Product");
    extra.setPriceInCents(500);
    extra.setDiscountedPriceInCents(500);
    extra.setType(product.getType());
    extra.setStore(store);
    extra = productRepository.save(extra);

    OutfitCreationProductDTO extraDTO = new OutfitCreationProductDTO();
    extraDTO.setId(extra.getId());
    extraDTO.setIndex(1);

    Outfit created = outfitService.create(buildCreationDTO());
    outfitService.addProduct(created.getId(), extraDTO);

    OutfitCreationProductDTO sortFirst = new OutfitCreationProductDTO();
    sortFirst.setId(product.getId());
    sortFirst.setIndex(1);

    OutfitCreationProductDTO sortSecond = new OutfitCreationProductDTO();
    sortSecond.setId(extra.getId());
    sortSecond.setIndex(0);

    assertDoesNotThrow(
        () -> outfitService.sortProducts(created.getId(), List.of(sortFirst, sortSecond)));
  }

  @Test
  void sortProducts_shouldThrowUnauthorizedException_whenNotAuthorized() {
    Outfit created = outfitService.create(buildCreationDTO());

    doThrow(new UnauthorizedException("Not authorized"))
        .when(userService)
        .assertUserOwnsStore(any());

    OutfitCreationProductDTO sortDTO = new OutfitCreationProductDTO();
    sortDTO.setId(product.getId());
    sortDTO.setIndex(0);

    assertThrows(
        UnauthorizedException.class,
        () -> outfitService.sortProducts(created.getId(), List.of(sortDTO)));
  }
}
