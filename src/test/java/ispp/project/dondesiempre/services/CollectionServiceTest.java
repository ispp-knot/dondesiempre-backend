package ispp.project.dondesiempre.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ispp.project.dondesiempre.dto.collection.CollectionCreationDTO;
import ispp.project.dondesiempre.dto.collection.CollectionResponseDTO;
import ispp.project.dondesiempre.dto.collection.CollectionUpdateDTO;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.models.products.ProductCollection;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.repositories.products.CollectionRepository;
import ispp.project.dondesiempre.repositories.products.ProductRepository;
import ispp.project.dondesiempre.repositories.stores.StoreRepository;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class CollectionServiceTest {

  @Mock private CollectionRepository collectionRepository;
  @Mock private ProductRepository productRepository;
  @Mock private StoreRepository storeRepository;

  @InjectMocks private CollectionService collectionService;

  private static final UUID STORE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final UUID OTHER_STORE_ID =
      UUID.fromString("00000000-0000-0000-0000-000000000002");
  private static final UUID COLLECTION_ID = UUID.fromString("00000000-0000-0000-0000-000000000010");
  private static final UUID PRODUCT_ID = UUID.fromString("00000000-0000-0000-0000-000000000100");
  private static final UUID MISSING_PRODUCT_ID =
      UUID.fromString("00000000-0000-0000-0000-000000000101");
  private static final UUID FOREIGN_PRODUCT_ID =
      UUID.fromString("00000000-0000-0000-0000-000000000199");

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
    collection.setDescription("Coleccion de primavera");
    collection.setStore(store);
    collection.setProducts(Set.of(product));
  }

  @Test
  void testGetByStore() {
    when(collectionRepository.findByStoreId(STORE_ID)).thenReturn(List.of(collection));

    List<CollectionResponseDTO> result = collectionService.getByStore(STORE_ID);

    assertEquals(1, result.size());
    assertEquals("Primavera", result.getFirst().getName());
    assertEquals(List.of(PRODUCT_ID), result.getFirst().getProductIds());
  }

  @Test
  void testGetById_found() {
    when(collectionRepository.findById(COLLECTION_ID)).thenReturn(Optional.of(collection));

    CollectionResponseDTO result = collectionService.getById(COLLECTION_ID);

    assertEquals(COLLECTION_ID, result.getId());
    assertEquals("Primavera", result.getName());
  }

  @Test
  void testGetById_notFound() {
    when(collectionRepository.findById(COLLECTION_ID)).thenReturn(Optional.empty());

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> collectionService.getById(COLLECTION_ID));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
  }

  @Test
  void testCreate_ok() {
    CollectionCreationDTO dto = new CollectionCreationDTO();
    dto.setName("Primavera");
    dto.setDescription("Coleccion de primavera");
    dto.setProductIds(Set.of(PRODUCT_ID));

    when(storeRepository.findById(STORE_ID)).thenReturn(Optional.of(store));
    when(collectionRepository.existsByNameAndStoreId("Primavera", STORE_ID)).thenReturn(false);
    when(productRepository.findByIdIn(Set.of(PRODUCT_ID))).thenReturn(List.of(product));
    when(collectionRepository.save(any(ProductCollection.class))).thenReturn(collection);

    CollectionResponseDTO result = collectionService.create(STORE_ID, dto);

    assertEquals("Primavera", result.getName());
    assertEquals(STORE_ID, result.getStoreId());
    assertEquals(List.of(PRODUCT_ID), result.getProductIds());
  }

  @Test
  void testCreate_storeNotFound() {
    CollectionCreationDTO dto = new CollectionCreationDTO();
    dto.setName("Primavera");
    dto.setProductIds(Set.of());

    when(storeRepository.findById(STORE_ID)).thenReturn(Optional.empty());

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> collectionService.create(STORE_ID, dto));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    verify(collectionRepository, never()).save(any(ProductCollection.class));
  }

  @Test
  void testCreate_conflict() {
    CollectionCreationDTO dto = new CollectionCreationDTO();
    dto.setName("Primavera");
    dto.setProductIds(Set.of());

    when(storeRepository.findById(STORE_ID)).thenReturn(Optional.of(store));
    when(collectionRepository.existsByNameAndStoreId("Primavera", STORE_ID)).thenReturn(true);

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> collectionService.create(STORE_ID, dto));

    assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    verify(collectionRepository, never()).save(any(ProductCollection.class));
  }

  @Test
  void testCreate_productsNotFound() {
    CollectionCreationDTO dto = new CollectionCreationDTO();
    dto.setName("Primavera");
    dto.setProductIds(Set.of(PRODUCT_ID, MISSING_PRODUCT_ID));

    when(storeRepository.findById(STORE_ID)).thenReturn(Optional.of(store));
    when(collectionRepository.existsByNameAndStoreId("Primavera", STORE_ID)).thenReturn(false);
    when(productRepository.findByIdIn(Set.of(PRODUCT_ID, MISSING_PRODUCT_ID)))
        .thenReturn(List.of(product));

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> collectionService.create(STORE_ID, dto));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
  }

  @Test
  void testCreate_productFromOtherStore() {
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

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> collectionService.create(STORE_ID, dto));

    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
  }

  @Test
  void testUpdate_ok() {
    CollectionUpdateDTO dto = new CollectionUpdateDTO();
    dto.setName("Verano");
    dto.setDescription("Coleccion de verano");
    dto.setProductIds(Set.of(PRODUCT_ID));

    ProductCollection updated = new ProductCollection();
    updated.setId(COLLECTION_ID);
    updated.setName("Verano");
    updated.setDescription("Coleccion de verano");
    updated.setStore(store);
    updated.setProducts(Set.of(product));

    when(collectionRepository.findById(COLLECTION_ID)).thenReturn(Optional.of(collection));
    when(collectionRepository.existsByNameAndStoreIdAndIdNot("Verano", STORE_ID, COLLECTION_ID))
        .thenReturn(false);
    when(productRepository.findByIdIn(Set.of(PRODUCT_ID))).thenReturn(List.of(product));
    when(collectionRepository.save(any(ProductCollection.class))).thenReturn(updated);

    CollectionResponseDTO result = collectionService.update(COLLECTION_ID, dto);

    assertEquals("Verano", result.getName());
    assertEquals(List.of(PRODUCT_ID), result.getProductIds());
  }

  @Test
  void testUpdate_conflict() {
    CollectionUpdateDTO dto = new CollectionUpdateDTO();
    dto.setName("Verano");
    dto.setProductIds(Set.of(PRODUCT_ID));

    when(collectionRepository.findById(COLLECTION_ID)).thenReturn(Optional.of(collection));
    when(collectionRepository.existsByNameAndStoreIdAndIdNot("Verano", STORE_ID, COLLECTION_ID))
        .thenReturn(true);

    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class, () -> collectionService.update(COLLECTION_ID, dto));

    assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
  }

  @Test
  void testUpdate_notFound() {
    CollectionUpdateDTO dto = new CollectionUpdateDTO();
    dto.setName("Verano");
    dto.setProductIds(Set.of());

    when(collectionRepository.findById(COLLECTION_ID)).thenReturn(Optional.empty());

    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class, () -> collectionService.update(COLLECTION_ID, dto));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
  }

  @Test
  void testDelete_ok() {
    when(collectionRepository.findById(COLLECTION_ID)).thenReturn(Optional.of(collection));

    collectionService.delete(COLLECTION_ID);

    verify(collectionRepository).delete(collection);
  }

  @Test
  void testDelete_notFound() {
    when(collectionRepository.findById(COLLECTION_ID)).thenReturn(Optional.empty());

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> collectionService.delete(COLLECTION_ID));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
  }
}
