package ispp.project.dondesiempre.services.outfits;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ispp.project.dondesiempre.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.models.outfits.Outfit;
import ispp.project.dondesiempre.models.outfits.OutfitProduct;
import ispp.project.dondesiempre.models.outfits.OutfitTag;
import ispp.project.dondesiempre.models.outfits.OutfitTagRelation;
import ispp.project.dondesiempre.models.outfits.dto.OutfitCreationDTO;
import ispp.project.dondesiempre.models.outfits.dto.OutfitCreationProductDTO;
import ispp.project.dondesiempre.models.outfits.dto.OutfitDTO;
import ispp.project.dondesiempre.models.outfits.dto.OutfitProductDTO;
import ispp.project.dondesiempre.models.outfits.dto.OutfitUpdateDTO;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.models.storefronts.Storefront;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.repositories.outfits.OutfitProductRepository;
import ispp.project.dondesiempre.repositories.outfits.OutfitRepository;
import ispp.project.dondesiempre.repositories.outfits.OutfitTagRelationRepository;
import ispp.project.dondesiempre.services.products.ProductService;
import ispp.project.dondesiempre.services.storefronts.StorefrontService;
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
  @Mock private OutfitProductRepository outfitProductRepository;
  @Mock private OutfitTagRelationRepository outfitTagRelationRepository;
  @Mock private ProductService productService;
  @Mock private StorefrontService storefrontService;
  @Mock private OutfitTagService outfitTagService;
  @Mock private ApplicationContext applicationContext;

  @InjectMocks private OutfitService outfitService;

  private UUID outfitId;
  private UUID productId;
  private UUID storefrontId;
  private UUID storeId;

  private Storefront storefront;
  private Store store;
  private Outfit outfit;
  private Product product;
  private OutfitProduct outfitProduct;

  @BeforeEach
  void setUp() {
    outfitId = UUID.randomUUID();
    productId = UUID.randomUUID();
    storefrontId = UUID.randomUUID();
    storeId = UUID.randomUUID();

    storefront = new Storefront();
    storefront.setId(storefrontId);

    store = new Store();
    store.setId(storeId);

    outfit = new Outfit();
    outfit.setId(outfitId);
    outfit.setName("Test Outfit");
    outfit.setDescription("Test Description");
    outfit.setIndex(0);
    outfit.setDiscountedPriceInCents(1000);
    outfit.setStorefront(storefront);

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
    lenient()
        .when(applicationContext.getBean(OutfitService.class))
        .thenReturn(outfitService);
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
  void shouldReturnOutfitDTO_whenIdExists() throws ResourceNotFoundException {
    when(outfitRepository.findById(outfitId)).thenReturn(Optional.of(outfit));
    when(outfitRepository.findOutfitTagsById(outfitId)).thenReturn(List.of("casual"));
    when(outfitRepository.findOutfitOutfitProductsById(outfitId))
        .thenReturn(List.of(outfitProduct));

    OutfitDTO result = outfitService.findByIdToDTO(outfitId);

    assertNotNull(result);
    assertEquals(outfitId, result.getId());
    assertEquals("casual", result.getTags().get(0));
    assertEquals("Test Product", result.getProducts().get(0).getName());
  }

  @Test
  void shouldThrowResourceNotFoundException_whenIdDoesNotExist_findByIdToDTO() {
    UUID nonExistentId = UUID.randomUUID();
    when(outfitRepository.findById(nonExistentId)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> outfitService.findByIdToDTO(nonExistentId));
  }

  @Test
  void shouldReturnListOfOutfitDTOs_whenStoreHasOutfits() {
    when(outfitRepository.findByStoreId(storeId)).thenReturn(List.of(outfit));
    when(outfitRepository.findOutfitTagsById(outfitId)).thenReturn(List.of("casual"));
    when(outfitRepository.findOutfitOutfitProductsById(outfitId))
        .thenReturn(List.of(outfitProduct));

    List<OutfitDTO> result = outfitService.findByStore(store);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(outfitId, result.get(0).getId());
    assertEquals("casual", result.get(0).getTags().get(0));
    assertEquals("Test Product", result.get(0).getProducts().get(0).getName());
  }

  @Test
  void shouldReturnEmptyList_whenStoreHasNoOutfits() {
    when(outfitRepository.findByStoreId(storeId)).thenReturn(new ArrayList<>());

    List<OutfitDTO> result = outfitService.findByStore(store);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  // --- create ---

  @Test
  void shouldCreateOutfit_whenValidData() {
    OutfitCreationProductDTO productDTO = new OutfitCreationProductDTO();
    productDTO.setId(productId);
    productDTO.setIndex(0);

    OutfitCreationDTO dto = new OutfitCreationDTO();
    dto.setName("New Outfit");
    dto.setDescription("Description");
    dto.setIndex(0);
    dto.setStorefrontId(storefrontId);
    dto.setTags(List.of("casual"));
    dto.setProducts(List.of(productDTO));

    OutfitTag tag = new OutfitTag();
    tag.setId(UUID.randomUUID());
    tag.setName("casual");

    when(storefrontService.findById(storefrontId)).thenReturn(storefront);
    when(productService.getProductById(productId)).thenReturn(product);
    when(outfitRepository.save(any())).thenReturn(outfit);
    when(outfitRepository.findById(outfitId)).thenReturn(Optional.of(outfit));
    when(outfitTagService.findByName("casual")).thenReturn(tag);
    when(outfitTagRelationRepository.save(any())).thenReturn(new OutfitTagRelation());
    when(outfitRepository.findOutfitProductsById(outfitId)).thenReturn(new ArrayList<>());
    when(outfitRepository.findOutfitProductIndicesById(outfitId)).thenReturn(new ArrayList<>());
    when(outfitProductRepository.save(any())).thenReturn(outfitProduct);
    when(outfitRepository.findOutfitTagsById(outfitId)).thenReturn(List.of("casual"));
    when(outfitRepository.findOutfitOutfitProductsById(outfitId))
        .thenReturn(List.of(outfitProduct));

    OutfitDTO result = outfitService.create(dto);

    assertNotNull(result);
    assertEquals(outfitId, result.getId());
    verify(outfitRepository, times(1)).save(any());
  }

  @Test
  void shouldThrowInvalidRequestException_whenProductsIsNull() {
    OutfitCreationDTO dto = new OutfitCreationDTO();
    dto.setName("New Outfit");
    dto.setIndex(0);
    dto.setStorefrontId(storefrontId);
    dto.setTags(List.of());
    dto.setProducts(null);

    when(storefrontService.findById(storefrontId)).thenReturn(storefront);

    assertThrows(InvalidRequestException.class, () -> outfitService.create(dto));
    verify(outfitRepository, never()).save(any());
  }

  @Test
  void shouldThrowInvalidRequestException_whenProductsIsEmpty() {
    OutfitCreationDTO dto = new OutfitCreationDTO();
    dto.setName("New Outfit");
    dto.setIndex(0);
    dto.setStorefrontId(storefrontId);
    dto.setTags(List.of());
    dto.setProducts(List.of());

    when(storefrontService.findById(storefrontId)).thenReturn(storefront);

    assertThrows(InvalidRequestException.class, () -> outfitService.create(dto));
    verify(outfitRepository, never()).save(any());
  }

  // --- update ---

  @Test
  void shouldUpdateOutfit_whenIdExists() {
    OutfitUpdateDTO dto = new OutfitUpdateDTO();
    dto.setName("Updated Outfit");
    dto.setDescription("Updated Description");
    dto.setDiscountedPriceInCents(2000);
    dto.setImage(null);
    dto.setIndex(1);

    when(outfitRepository.findById(outfitId)).thenReturn(Optional.of(outfit));
    when(outfitRepository.save(any())).thenReturn(outfit);
    when(outfitRepository.findOutfitTagsById(outfitId)).thenReturn(List.of());
    when(outfitRepository.findOutfitOutfitProductsById(outfitId)).thenReturn(List.of());

    OutfitDTO result = outfitService.update(outfitId, dto);

    assertNotNull(result);
    assertEquals("Updated Outfit", result.getName());
    verify(outfitRepository, times(1)).save(outfit);
  }

  @Test
  void shouldThrowResourceNotFoundException_whenUpdatingNonExistentOutfit() {
    UUID nonExistentId = UUID.randomUUID();
    OutfitUpdateDTO dto = new OutfitUpdateDTO();
    dto.setName("Updated Outfit");

    when(outfitRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> outfitService.update(nonExistentId, dto));
    verify(outfitRepository, never()).save(any());
  }

  // --- addTag ---

  @Test
  void shouldAddExistingTag_whenTagAlreadyExists() {
    OutfitTag tag = new OutfitTag();
    tag.setId(UUID.randomUUID());
    tag.setName("casual");

    when(outfitRepository.findById(outfitId)).thenReturn(Optional.of(outfit));
    when(outfitTagService.findByName("casual")).thenReturn(tag);
    when(outfitTagRelationRepository.save(any())).thenReturn(new OutfitTagRelation());

    String result = outfitService.addTag(outfitId, "casual");

    assertEquals("casual", result);
    verify(outfitTagService, never()).create(any());
    verify(outfitTagRelationRepository, times(1)).save(any());
  }

  @Test
  void shouldCreateAndAddTag_whenTagDoesNotExist() {
    OutfitTag newTag = new OutfitTag();
    newTag.setId(UUID.randomUUID());
    newTag.setName("new-tag");

    when(outfitRepository.findById(outfitId)).thenReturn(Optional.of(outfit));
    when(outfitTagService.findByName("new-tag"))
        .thenThrow(new ResourceNotFoundException("Tag 'new-tag' not found."));
    when(outfitTagService.create("new-tag")).thenReturn(newTag);
    when(outfitTagRelationRepository.save(any())).thenReturn(new OutfitTagRelation());

    String result = outfitService.addTag(outfitId, "new-tag");

    assertEquals("new-tag", result);
    verify(outfitTagService, times(1)).create("new-tag");
    verify(outfitTagRelationRepository, times(1)).save(any());
  }

  // --- addProduct ---

  @Test
  void shouldAddProduct_whenValidData() {
    OutfitCreationProductDTO dto = new OutfitCreationProductDTO();
    dto.setId(productId);
    dto.setIndex(0);

    when(outfitRepository.findById(outfitId)).thenReturn(Optional.of(outfit));
    when(productService.getProductById(productId)).thenReturn(product);
    when(outfitRepository.findOutfitProductsById(outfitId)).thenReturn(new ArrayList<>());
    when(outfitRepository.findOutfitProductIndicesById(outfitId)).thenReturn(new ArrayList<>());
    when(outfitProductRepository.save(any())).thenReturn(outfitProduct);

    OutfitProductDTO result = outfitService.addProduct(outfitId, dto);

    assertNotNull(result);
    assertEquals("Test Product", result.getName());
    verify(outfitProductRepository, times(1)).save(any());
  }

  @Test
  void shouldThrowInvalidRequestException_whenProductsFromDifferentStores() {
    Store otherStore = new Store();
    otherStore.setId(UUID.randomUUID());

    Product otherProduct = new Product();
    otherProduct.setId(UUID.randomUUID());
    otherProduct.setStore(otherStore);

    OutfitCreationProductDTO dto = new OutfitCreationProductDTO();
    dto.setId(otherProduct.getId());
    dto.setIndex(1);

    when(outfitRepository.findById(outfitId)).thenReturn(Optional.of(outfit));
    when(productService.getProductById(otherProduct.getId())).thenReturn(otherProduct);
    // Existing product belongs to 'store'; new product belongs to 'otherStore'
    when(outfitRepository.findOutfitProductsById(outfitId))
        .thenReturn(new ArrayList<>(List.of(product)));

    assertThrows(InvalidRequestException.class, () -> outfitService.addProduct(outfitId, dto));
    verify(outfitProductRepository, never()).save(any());
  }

  @Test
  void shouldThrowInvalidRequestException_whenDuplicateProduct() {
    // Adding the same product that already exists in the outfit
    OutfitCreationProductDTO dto = new OutfitCreationProductDTO();
    dto.setId(productId);
    dto.setIndex(1);

    when(outfitRepository.findById(outfitId)).thenReturn(Optional.of(outfit));
    when(productService.getProductById(productId)).thenReturn(product);
    // 'product' already in the outfit; adding it again makes distinct count (1) < total (2)
    when(outfitRepository.findOutfitProductsById(outfitId))
        .thenReturn(new ArrayList<>(List.of(product)));
    when(outfitRepository.findOutfitProductIndicesById(outfitId))
        .thenReturn(new ArrayList<>(List.of(0)));

    assertThrows(InvalidRequestException.class, () -> outfitService.addProduct(outfitId, dto));
    verify(outfitProductRepository, never()).save(any());
  }

  // --- delete ---

  @Test
  void shouldCallDeleteById_whenDeleteInvoked() {
    outfitService.delete(outfitId);

    verify(outfitRepository, times(1)).deleteById(outfitId);
  }
}
