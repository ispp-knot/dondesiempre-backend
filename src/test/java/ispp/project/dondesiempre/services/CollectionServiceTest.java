package ispp.project.dondesiempre.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ispp.project.dondesiempre.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.exceptions.RequestConflictException;
import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.models.collections.ProductCollection;
import ispp.project.dondesiempre.models.collections.dto.CollectionCreationDTO;
import ispp.project.dondesiempre.models.collections.dto.CollectionUpdateDTO;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.repositories.collections.CollectionRepository;
import ispp.project.dondesiempre.repositories.products.ProductRepository;
import ispp.project.dondesiempre.repositories.stores.StoreRepository;
import ispp.project.dondesiempre.services.collections.CollectionService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CollectionServiceTest {

  @Mock private CollectionRepository collectionRepository;
  @Mock private ProductRepository productRepository;
  @Mock private StoreRepository storeRepository;

  @InjectMocks private CollectionService collectionService;

  private static final UUID STORE_ID = UUID.randomUUID();
  private static final UUID OTHER_STORE_ID = UUID.randomUUID();
  private static final UUID COLLECTION_ID = UUID.randomUUID();
  private static final UUID PRODUCT_ID = UUID.randomUUID();
  private static final UUID FOREIGN_PRODUCT_ID = UUID.randomUUID();

  private Store store;
  private Product product;
  private ProductCollection collection;

  @BeforeEach
  void setUp() {
    store = new Store();
    store.setId(STORE_ID);
    store.setName("Tienda Test");

    product = new Product();
    product.setId(PRODUCT_ID);
    product.setStore(store);

    collection = new ProductCollection();
    collection.setId(COLLECTION_ID);
    collection.setName("Primavera");
    collection.setStore(store);
    collection.setProducts(Set.of(product));
  }

  @Test
  void shouldReturnCollections_WhenGetByStore() {
    when(collectionRepository.findByStoreId(STORE_ID)).thenReturn(List.of(collection));
    List<ProductCollection> result = collectionService.getByStore(STORE_ID);
    assertEquals(1, result.size());
  }

  @Test
  void shouldReturnCollection_WhenGetByIdExists() {
    when(collectionRepository.findById(COLLECTION_ID)).thenReturn(Optional.of(collection));
    ProductCollection result = collectionService.getById(COLLECTION_ID);
    assertEquals(COLLECTION_ID, result.getId());
  }

  @Test
  void shouldThrowNotFound_WhenGetByIdMissing() {
    when(collectionRepository.findById(COLLECTION_ID)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> collectionService.getById(COLLECTION_ID));
  }

  @Test
  void shouldCreateCollection_WhenDataIsValid() {
    CollectionCreationDTO dto = new CollectionCreationDTO();
    dto.setName("Primavera");
    dto.setProductIds(Set.of(PRODUCT_ID));

    when(storeRepository.findById(STORE_ID)).thenReturn(Optional.of(store));
    when(collectionRepository.existsByNameAndStoreId("Primavera", STORE_ID)).thenReturn(false);
    when(productRepository.findByIdIn(Set.of(PRODUCT_ID))).thenReturn(List.of(product));
    when(collectionRepository.save(any(ProductCollection.class))).thenReturn(collection);

    ProductCollection result = collectionService.create(STORE_ID, dto);
    assertEquals("Primavera", result.getName());
  }

  @Test
  void shouldThrowNotFound_WhenCreateAndStoreMissing() {
    CollectionCreationDTO dto = new CollectionCreationDTO();
    dto.setName("Primavera");
    when(storeRepository.findById(STORE_ID)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> collectionService.create(STORE_ID, dto));
    verify(collectionRepository, never()).save(any(ProductCollection.class));
  }

  @Test
  void shouldThrowConflict_WhenCreateAndNameAlreadyExists() {
    CollectionCreationDTO dto = new CollectionCreationDTO();
    dto.setName("Primavera");
    when(storeRepository.findById(STORE_ID)).thenReturn(Optional.of(store));
    when(collectionRepository.existsByNameAndStoreId("Primavera", STORE_ID)).thenReturn(true);

    assertThrows(RequestConflictException.class, () -> collectionService.create(STORE_ID, dto));
  }

  @Test
  void shouldThrowBadRequest_WhenCreateWithProductsFromAnotherStore() {
    Store otherStore = new Store();
    otherStore.setId(OTHER_STORE_ID);
    Product foreignProduct = new Product();
    foreignProduct.setId(FOREIGN_PRODUCT_ID);
    foreignProduct.setStore(otherStore);

    CollectionCreationDTO dto = new CollectionCreationDTO();
    dto.setName("Primavera");
    dto.setProductIds(Set.of(FOREIGN_PRODUCT_ID));

    when(storeRepository.findById(STORE_ID)).thenReturn(Optional.of(store));
    when(collectionRepository.existsByNameAndStoreId("Primavera", STORE_ID)).thenReturn(false);
    when(productRepository.findByIdIn(Set.of(FOREIGN_PRODUCT_ID)))
        .thenReturn(List.of(foreignProduct));

    assertThrows(InvalidRequestException.class, () -> collectionService.create(STORE_ID, dto));
  }

  @Test
  void shouldUpdateCollection_WhenDataIsValid() {
    CollectionUpdateDTO dto = new CollectionUpdateDTO();
    dto.setName("Verano");
    dto.setProductIds(Set.of(PRODUCT_ID));

    when(collectionRepository.findById(COLLECTION_ID)).thenReturn(Optional.of(collection));
    when(collectionRepository.existsByNameAndStoreIdAndIdNot("Verano", STORE_ID, COLLECTION_ID))
        .thenReturn(false);
    when(productRepository.findByIdIn(Set.of(PRODUCT_ID))).thenReturn(List.of(product));
    when(collectionRepository.save(any(ProductCollection.class))).thenReturn(collection);

    ProductCollection result = collectionService.update(COLLECTION_ID, dto);
    assertEquals(COLLECTION_ID, result.getId());
  }

  @Test
  void shouldThrowConflict_WhenUpdateAndNameAlreadyExists() {
    CollectionUpdateDTO dto = new CollectionUpdateDTO();
    dto.setName("Verano");
    when(collectionRepository.findById(COLLECTION_ID)).thenReturn(Optional.of(collection));
    when(collectionRepository.existsByNameAndStoreIdAndIdNot("Verano", STORE_ID, COLLECTION_ID))
        .thenReturn(true);

    assertThrows(
        RequestConflictException.class, () -> collectionService.update(COLLECTION_ID, dto));
  }

  @Test
  void shouldDeleteCollection_WhenDeleteExisting() {
    when(collectionRepository.findById(COLLECTION_ID)).thenReturn(Optional.of(collection));
    collectionService.delete(COLLECTION_ID);
    verify(collectionRepository).delete(collection);
  }

  @Test
  void shouldThrowNotFound_WhenDeleteMissing() {
    when(collectionRepository.findById(COLLECTION_ID)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> collectionService.delete(COLLECTION_ID));
  }
}
