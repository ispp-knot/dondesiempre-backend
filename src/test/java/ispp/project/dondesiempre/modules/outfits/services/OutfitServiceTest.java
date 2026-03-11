package ispp.project.dondesiempre.modules.outfits.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.auth.services.AuthService;
import ispp.project.dondesiempre.modules.common.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.outfits.dtos.OutfitCreationDTO;
import ispp.project.dondesiempre.modules.outfits.dtos.OutfitCreationProductDTO;
import ispp.project.dondesiempre.modules.outfits.dtos.OutfitUpdateDTO;
import ispp.project.dondesiempre.modules.outfits.models.Outfit;
import ispp.project.dondesiempre.modules.outfits.models.OutfitProduct;
import ispp.project.dondesiempre.modules.outfits.models.OutfitTag;
import ispp.project.dondesiempre.modules.outfits.models.OutfitTagRelation;
import ispp.project.dondesiempre.modules.outfits.repositories.OutfitRepository;
import ispp.project.dondesiempre.modules.products.models.Product;
import ispp.project.dondesiempre.modules.products.services.ProductService;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.models.Storefront;
import ispp.project.dondesiempre.modules.stores.services.StoreService;
import ispp.project.dondesiempre.modules.stores.services.StorefrontService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

@ExtendWith(MockitoExtension.class)
class OutfitServiceTest {

  @Mock private OutfitRepository outfitRepository;
  @Mock private OutfitProductService outfitProductService;
  @Mock private OutfitTagRelationService outfitTagRelationService;
  @Mock private OutfitTagService outfitTagService;
  @Mock private ProductService productService;
  @Mock private AuthService authService;
  @Mock private StorefrontService storefrontService;
  @Mock private StoreService storeService;
  @Mock private ApplicationContext applicationContext;

  @InjectMocks private OutfitService outfitService;

  private UUID outfitId;
  private UUID productId;
  private UUID storefrontId;
  private UUID storeId;
  private UUID userId;

  private Storefront storefront;
  private Store store;
  private Outfit outfit;
  private Product product;
  private OutfitProduct outfitProduct;
  private User user;

  @BeforeEach
  void setUp() {
    outfitId = UUID.randomUUID();
    productId = UUID.randomUUID();
    storefrontId = UUID.randomUUID();
    storeId = UUID.randomUUID();
    userId = UUID.randomUUID();

    user = new User();
    user.setId(userId);

    storefront = new Storefront();
    storefront.setId(storefrontId);

    store = new Store();
    store.setId(storeId);
    store.setUser(user);
    outfit = new Outfit();
    outfit.setId(outfitId);
    outfit.setName("Test Outfit");
    outfit.setDescription("Test Description");
    outfit.setIndex(0);
    outfit.setDiscountedPriceInCents(1000);
    outfit.setStore(store);

    product = new Product();
    product.setId(productId);
    product.setName("Test Product");
    product.setDiscountedPriceInCents(1000);
    product.setStore(store);

    outfitProduct = new OutfitProduct();
    outfitProduct.setId(UUID.randomUUID());
    outfitProduct.setIndex(0);
    outfitProduct.setOutfit(outfit);
    outfitProduct.setProduct(product);

    // Lenient: not all tests call methods that go through ApplicationContext
    lenient().when(applicationContext.getBean(OutfitService.class)).thenReturn(outfitService);
  }

  // --- findById ---
  @Test
  void shouldReturnOutfit_whenIdExists() throws ResourceNotFoundException {
    when(outfitRepository.findById(outfitId)).thenReturn(Optional.of(outfit));

    Outfit result = outfitService.findById(outfitId);

    assertNotNull(result);
    assertEquals(outfitId, result.getId());
  }

  @Test
  void shouldThrowResourceNotFoundException_whenIdDoesNotExist() {
    UUID nonExistentId = UUID.randomUUID();
    when(outfitRepository.findById(nonExistentId)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> outfitService.findById(nonExistentId));
  }

  @Test
  void shouldReturnListOfOutfitDTOs_whenStoreHasOutfits() {
    when(outfitRepository.findByStoreIdOrderByIndexAsc(storeId)).thenReturn(List.of(outfit));

    List<Outfit> result = outfitService.findByStore(store);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(outfitId, result.get(0).getId());
    assertEquals("Test Outfit", result.get(0).getName());
  }

  @Test
  void shouldReturnEmptyList_whenStoreHasNoOutfits() {
    when(outfitRepository.findByStoreIdOrderByIndexAsc(storeId)).thenReturn(new ArrayList<>());

    List<Outfit> result = outfitService.findByStore(store);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  // --- create ---

  private OutfitCreationProductDTO createValidProductDTO() {
    OutfitCreationProductDTO productDTO = new OutfitCreationProductDTO();
    productDTO.setProductId(productId);
    productDTO.setIndex(0);
    return productDTO;
  }

  private OutfitCreationDTO createValidOutfitCreationDTO() {
    OutfitCreationProductDTO productDTO = createValidProductDTO();

    OutfitCreationDTO dto = new OutfitCreationDTO();
    dto.setName("New Outfit");
    dto.setDescription("Description");
    dto.setIndex(0);
    dto.setTags(List.of("casual"));
    dto.setProducts(List.of(productDTO));
    return dto;
  }

  @Test
  void shouldCreateOutfit_whenValidData() {
    OutfitCreationDTO dto = createValidOutfitCreationDTO();

    OutfitTag tag = new OutfitTag();
    tag.setId(UUID.randomUUID());
    tag.setName("casual");

    when(storeService.findById(storeId)).thenReturn(store);
    when(productService.getProductById(productId)).thenReturn(product);
    when(outfitRepository.save(any())).thenReturn(outfit);
    when(outfitRepository.findById(outfitId)).thenReturn(Optional.of(outfit));
    when(outfitTagService.findOrCreate("casual")).thenReturn(tag);
    when(outfitTagRelationService.save(any())).thenReturn(new OutfitTagRelation());
    when(outfitProductService.findOutfitProductIndicesById(outfitId)).thenReturn(new ArrayList<>());
    when(outfitProductService.save(any())).thenReturn(outfitProduct);

    Outfit result = outfitService.create(storeId, dto, null);

    assertNotNull(result);
    assertNotNull(result);
    assertEquals(outfitId, result.getId());
    verify(outfitRepository, times(1)).save(any());
    assertEquals(1000, result.getDiscountedPriceInCents());
  }

  @Test
  void shouldThrowInvalidRequestException_whenProductsIsNull() {
    OutfitCreationDTO dto = new OutfitCreationDTO();
    dto.setName("New Outfit");
    dto.setIndex(0);
    dto.setTags(List.of());
    dto.setProducts(null);

    when(storeService.findById(storeId)).thenReturn(store);

    assertThrows(InvalidRequestException.class, () -> outfitService.create(storeId, dto, null));
    verify(outfitRepository, never()).save(any());
  }

  @Test
  void shouldThrowInvalidRequestException_whenProductsIsEmpty() {
    OutfitCreationDTO dto = new OutfitCreationDTO();
    dto.setName("New Outfit");
    dto.setIndex(0);
    dto.setTags(List.of());
    dto.setProducts(List.of());

    when(storeService.findById(storeId)).thenReturn(store);

    assertThrows(InvalidRequestException.class, () -> outfitService.create(storeId, dto, null));
    verify(outfitRepository, never()).save(any());
  }

  // --- update ---

  @Test
  void shouldUpdateOutfit_whenIdExists() {
    OutfitUpdateDTO dto = new OutfitUpdateDTO();
    dto.setName("Updated Outfit");
    dto.setDescription("Updated Description");
    dto.setDiscountedPriceInCents(2000);
    dto.setIndex(1);

    when(outfitRepository.findById(outfitId)).thenReturn(Optional.of(outfit));
    when(outfitRepository.save(any())).thenReturn(outfit);

    Outfit result = outfitService.update(outfitId, dto, null);

    assertNotNull(result);
    assertEquals("Updated Outfit", result.getName());
    verify(outfitRepository, times(1)).save(outfit);
  }

  // --- addTag ---

  @Test
  void shouldCreateAndAddTag_whenTagDoesNotExist() {
    OutfitTag newTag = new OutfitTag();
    newTag.setId(UUID.randomUUID());
    newTag.setName("new-tag");

    when(outfitRepository.findById(outfitId)).thenReturn(Optional.of(outfit));
    when(outfitTagService.findOrCreate("new-tag")).thenReturn(newTag);
    when(outfitTagRelationService.save(any())).thenReturn(new OutfitTagRelation());

    String result = outfitService.addTag(outfitId, "new-tag");

    assertEquals("new-tag", result);
    verify(outfitTagService, times(1)).findOrCreate("new-tag");
    verify(outfitTagRelationService, times(1)).save(any());
  }

  // --- addProduct ---

  @Test
  void shouldAddProduct_whenValidData() {
    OutfitCreationProductDTO dto = new OutfitCreationProductDTO();
    dto.setProductId(productId);
    dto.setIndex(0);

    when(outfitRepository.findById(outfitId)).thenReturn(Optional.of(outfit));
    when(productService.getProductById(productId)).thenReturn(product); //
    when(productService.getOutfitProductsById(outfitId)).thenReturn(new ArrayList<>());
    when(outfitProductService.findOutfitProductIndicesById(outfitId)).thenReturn(new ArrayList<>());
    when(outfitProductService.save(any())).thenReturn(outfitProduct);

    OutfitProduct result = outfitService.addProduct(outfitId, dto);

    assertNotNull(result);
    assertEquals("Test Product", result.getProduct().getName());
    verify(outfitProductService, times(1)).save(any());
  }

  @Test
  void shouldThrowInvalidRequestException_whenProductsFromDifferentStores() {
    Store otherStore = new Store();
    otherStore.setId(UUID.randomUUID());

    Product otherProduct = new Product();
    otherProduct.setId(UUID.randomUUID());
    otherProduct.setStore(otherStore);

    OutfitCreationProductDTO dto = new OutfitCreationProductDTO();
    dto.setProductId(otherProduct.getId());
    dto.setIndex(1);

    when(outfitRepository.findById(outfitId)).thenReturn(Optional.of(outfit));
    when(productService.getProductById(otherProduct.getId())).thenReturn(otherProduct);
    // Existing product belongs to 'store'; new product belongs to 'otherStore'
    when(productService.getOutfitProductsById(outfitId))
        .thenReturn(new ArrayList<>(List.of(product)));

    assertThrows(InvalidRequestException.class, () -> outfitService.addProduct(outfitId, dto));
    verify(outfitProductService, never()).save(any());
  }

  @Test
  void shouldThrowInvalidRequestException_whenDuplicateProduct() {
    // Adding the same product that already exists in the outfit
    OutfitCreationProductDTO dto = new OutfitCreationProductDTO();
    dto.setProductId(productId);
    dto.setIndex(1);

    when(outfitRepository.findById(outfitId)).thenReturn(Optional.of(outfit));
    when(productService.getProductById(productId)).thenReturn(product);
    // 'product' already in the outfit; adding it again makes distinct count (1) <
    // total (2)
    when(productService.getOutfitProductsById(outfitId))
        .thenReturn(new ArrayList<>(List.of(product)));
    when(outfitProductService.findOutfitProductIndicesById(outfitId))
        .thenReturn(new ArrayList<>(List.of(0)));

    assertThrows(InvalidRequestException.class, () -> outfitService.addProduct(outfitId, dto));
    verify(outfitProductService, never()).save(any());
  }

  // --- delete ---
  @Test
  void shouldDeleteProductsTagsAndOutfit_whenDeleteInvoked() {

    // Simulamos que el outfit existe
    when(outfitRepository.findById(outfitId)).thenReturn(Optional.of(outfit));

    // Simulamos que hay un producto asociado
    when(outfitProductService.findOutfitProductsById(outfitId)).thenReturn(List.of(outfitProduct));

    // Simulamos que NO hay tags asociados
    when(outfitTagRelationService.findOutfitTagsById(outfitId)).thenReturn(List.of());

    // Evitamos que falle la validación de ownership
    doNothing().when(authService).assertUserOwnsStore(any());

    // Ejecutamos
    outfitService.delete(outfitId);

    // Verificamos que se elimina el outfitProduct concreto
    verify(outfitProductService, times(1)).deleteById(outfitProduct.getId());

    // No debería eliminar tags
    verify(outfitTagRelationService, never()).deleteById(any());

    // Finalmente se elimina el outfit
    verify(outfitRepository, times(1)).deleteById(outfitId);
  }

  // --- sortProducts ---

  @Test
  void shouldSortProducts_whenValidData() throws ResourceNotFoundException {
    UUID productId2 = UUID.randomUUID();
    Product product2 = new Product();
    product2.setId(productId2);
    product2.setName("Test Product 2");

    OutfitProduct outfitProduct2 = new OutfitProduct();
    outfitProduct2.setId(UUID.randomUUID());
    outfitProduct2.setIndex(1);
    outfitProduct2.setOutfit(outfit);
    outfitProduct2.setProduct(product2);

    OutfitCreationProductDTO dto1 = new OutfitCreationProductDTO();
    dto1.setProductId(productId);
    dto1.setIndex(1); // Changed index

    OutfitCreationProductDTO dto2 = new OutfitCreationProductDTO();
    dto2.setProductId(productId2);
    dto2.setIndex(0); // Changed index

    List<OutfitCreationProductDTO> products = List.of(dto1, dto2);
    List<OutfitProduct> relations = new ArrayList<>();
    relations.add(outfitProduct);
    relations.add(outfitProduct2);

    when(outfitRepository.findById(outfitId)).thenReturn(Optional.of(outfit));
    when(outfitProductService.findOutfitProductsById(outfitId)).thenReturn(relations);
    when(outfitProductService.saveAll(relations)).thenReturn(relations);

    outfitService.sortProducts(outfitId, products);

    assertEquals(1, outfitProduct.getIndex());
    assertEquals(0, outfitProduct2.getIndex());
    verify(outfitProductService, times(1)).saveAll(relations);
  }

  @Test
  void shouldThrowResourceNotFoundException_whenProductNotInOutfit() {
    UUID productId2 = UUID.randomUUID();
    Product product2 = new Product();
    product2.setId(productId2);
    product2.setName("Test Product 2");

    OutfitProduct outfitProduct2 = new OutfitProduct();
    outfitProduct2.setId(UUID.randomUUID());
    outfitProduct2.setIndex(1);
    outfitProduct2.setOutfit(outfit);
    outfitProduct2.setProduct(product2);

    // Only providing index update for one product, but outfit has two products
    OutfitCreationProductDTO dto1 = new OutfitCreationProductDTO();
    dto1.setProductId(productId);
    dto1.setIndex(0);

    List<OutfitCreationProductDTO> products = List.of(dto1); // Missing product2
    List<OutfitProduct> relations = new ArrayList<>();
    relations.add(outfitProduct);
    relations.add(outfitProduct2); // This relation won't find a match in products list

    when(outfitRepository.findById(outfitId)).thenReturn(Optional.of(outfit));
    when(outfitProductService.findOutfitProductsById(outfitId)).thenReturn(relations);

    assertThrows(
        ResourceNotFoundException.class, () -> outfitService.sortProducts(outfitId, products));
    verify(outfitProductService, never()).saveAll(any());
  }

  // --- removeProduct ---

  @Test
  void shouldRemoveProduct_whenProductBelongsToOutfit() throws ResourceNotFoundException {
    when(outfitRepository.findById(outfitId)).thenReturn(Optional.of(outfit));
    when(outfitProductService.findProductRelation(outfitId, productId)).thenReturn(outfitProduct);

    outfitService.removeProduct(outfitId, product);

    verify(outfitProductService, times(1)).delete(outfitProduct);
  }

  @Test
  void shouldThrowResourceNotFoundException_whenProductDoesNotBelongToOutfit() {
    UUID nonExistentProductId = UUID.randomUUID();
    Product nonExistentProduct = new Product();
    nonExistentProduct.setId(nonExistentProductId);

    when(outfitRepository.findById(outfitId)).thenReturn(Optional.of(outfit));
    when(outfitProductService.findProductRelation(outfitId, nonExistentProductId))
        .thenThrow(new ResourceNotFoundException("Requested product does not belong to outfit."));

    assertThrows(
        ResourceNotFoundException.class,
        () -> outfitService.removeProduct(outfitId, nonExistentProduct));
    verify(outfitProductService, never()).delete(any());
  }

  @Test
  void shouldfindTagsByOutfitId() throws ResourceNotFoundException {

    when(outfitTagService.findOutfitTagsById(outfitId)).thenReturn(List.of("casual", "summer"));

    List<String> result = outfitService.findTagsByOutfitId(outfitId);
    assertNotNull(result);
    verify(outfitTagService, times(1)).findOutfitTagsById(outfitId);
  }

  @Test
  void shouldfindOutfitProductsByOutfitId() throws ResourceNotFoundException {

    when(outfitProductService.findOutfitProductsById(outfitId)).thenReturn(List.of(outfitProduct));

    List<OutfitProduct> result = outfitService.findOutfitProductsByOutfitId(outfitId);
    assertNotNull(result);
    verify(outfitProductService, times(1)).findOutfitProductsById(outfitId);
  }

  @Test
  void shouldRemoveTag_whenTagBelongsToOutfit() throws ResourceNotFoundException {
    UUID tagId = UUID.randomUUID();
    OutfitTag tag = new OutfitTag();
    String tagName = "casual";
    tag.setId(tagId);
    tag.setName(tagName);

    OutfitTagRelation relation = new OutfitTagRelation();
    relation.setId(UUID.randomUUID());
    relation.setOutfit(outfit);
    relation.setTag(tag);

    when(outfitRepository.findById(outfitId)).thenReturn(Optional.of(outfit));
    doNothing().when(authService).assertUserOwnsStore(any());
    when(outfitTagService.findByName(tagName)).thenReturn(tag);
    when(outfitTagRelationService.findTagRelation(outfitId, tagId)).thenReturn(relation);

    outfitService.removeTag(outfitId, tagName);

    verify(outfitTagRelationService, times(1)).delete(relation);
  }

  @Test
  void shouldThrowResourceNotFoundException_whenTagDoesNotBelongToOutfit() {
    String tagName = "non-existent-tag";
    UUID nonExistentTagId = UUID.randomUUID();
    OutfitTag nonExistentTag = new OutfitTag();
    nonExistentTag.setId(nonExistentTagId);

    when(outfitRepository.findById(outfitId)).thenReturn(Optional.of(outfit));
    doNothing().when(authService).assertUserOwnsStore(any());
    when(outfitTagService.findByName(tagName)).thenReturn(nonExistentTag); // Tag doesn't exist
    when(outfitTagRelationService.findTagRelation(outfitId, nonExistentTagId))
        .thenThrow(
            new ResourceNotFoundException(
                "Requested tag does not belong to outfit.")); // No relation found

    assertThrows(ResourceNotFoundException.class, () -> outfitService.removeTag(outfitId, tagName));
    verify(outfitTagRelationService, never()).delete(any());
  }
}
