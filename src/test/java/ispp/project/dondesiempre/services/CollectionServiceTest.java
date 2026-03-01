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

  private Store store;
  private Product product;
  private ProductCollection collection;

  @BeforeEach
  void setUp() {
    store = new Store();
    store.setId(1);
    store.setName("Tienda Test");

    product = new Product();
    product.setId(10);
    product.setStore(store);

    collection = new ProductCollection();
    collection.setId(1);
    collection.setName("Primavera");
    collection.setDescription("Coleccion de primavera");
    collection.setStore(store);
    collection.setProducts(Set.of(product));
  }

  @Test
  void testGetByStore() {
    when(collectionRepository.findByStoreId(1)).thenReturn(List.of(collection));

    List<CollectionResponseDTO> result = collectionService.getByStore(1);

    assertEquals(1, result.size());
    assertEquals("Primavera", result.getFirst().getName());
    assertEquals(List.of(10), result.getFirst().getProductIds());
  }

  @Test
  void testGetById_found() {
    when(collectionRepository.findById(1)).thenReturn(Optional.of(collection));

    CollectionResponseDTO result = collectionService.getById(1);

    assertEquals(1, result.getId());
    assertEquals("Primavera", result.getName());
  }

  @Test
  void testGetById_notFound() {
    when(collectionRepository.findById(99)).thenReturn(Optional.empty());

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> collectionService.getById(99));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
  }

  @Test
  void testCreate_ok() {
    CollectionCreationDTO dto = new CollectionCreationDTO();
    dto.setName("Primavera");
    dto.setDescription("Coleccion de primavera");
    dto.setProductIds(Set.of(10));

    when(storeRepository.findById(1)).thenReturn(Optional.of(store));
    when(collectionRepository.existsByNameAndStoreId("Primavera", 1)).thenReturn(false);
    when(productRepository.findByIdIn(Set.of(10))).thenReturn(List.of(product));
    when(collectionRepository.save(any(ProductCollection.class))).thenReturn(collection);

    CollectionResponseDTO result = collectionService.create(1, dto);

    assertEquals("Primavera", result.getName());
    assertEquals(1, result.getStoreId());
    assertEquals(List.of(10), result.getProductIds());
  }

  @Test
  void testCreate_storeNotFound() {
    CollectionCreationDTO dto = new CollectionCreationDTO();
    dto.setName("Primavera");
    dto.setProductIds(Set.of());

    when(storeRepository.findById(99)).thenReturn(Optional.empty());

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> collectionService.create(99, dto));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    verify(collectionRepository, never()).save(any(ProductCollection.class));
  }

  @Test
  void testCreate_conflict() {
    CollectionCreationDTO dto = new CollectionCreationDTO();
    dto.setName("Primavera");
    dto.setProductIds(Set.of());

    when(storeRepository.findById(1)).thenReturn(Optional.of(store));
    when(collectionRepository.existsByNameAndStoreId("Primavera", 1)).thenReturn(true);

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> collectionService.create(1, dto));

    assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    verify(collectionRepository, never()).save(any(ProductCollection.class));
  }

  @Test
  void testCreate_productsNotFound() {
    CollectionCreationDTO dto = new CollectionCreationDTO();
    dto.setName("Primavera");
    dto.setProductIds(Set.of(10, 11));

    when(storeRepository.findById(1)).thenReturn(Optional.of(store));
    when(collectionRepository.existsByNameAndStoreId("Primavera", 1)).thenReturn(false);
    when(productRepository.findByIdIn(Set.of(10, 11))).thenReturn(List.of(product));

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> collectionService.create(1, dto));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
  }

  @Test
  void testCreate_productFromOtherStore() {
    Store otherStore = new Store();
    otherStore.setId(2);

    Product foreignProduct = new Product();
    foreignProduct.setId(99);
    foreignProduct.setStore(otherStore);

    CollectionCreationDTO dto = new CollectionCreationDTO();
    dto.setName("Primavera");
    dto.setProductIds(Set.of(99));

    when(storeRepository.findById(1)).thenReturn(Optional.of(store));
    when(collectionRepository.existsByNameAndStoreId("Primavera", 1)).thenReturn(false);
    when(productRepository.findByIdIn(Set.of(99))).thenReturn(List.of(foreignProduct));

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> collectionService.create(1, dto));

    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
  }

  @Test
  void testUpdate_ok() {
    CollectionUpdateDTO dto = new CollectionUpdateDTO();
    dto.setName("Verano");
    dto.setDescription("Coleccion de verano");
    dto.setProductIds(Set.of(10));

    ProductCollection updated = new ProductCollection();
    updated.setId(1);
    updated.setName("Verano");
    updated.setDescription("Coleccion de verano");
    updated.setStore(store);
    updated.setProducts(Set.of(product));

    when(collectionRepository.findById(1)).thenReturn(Optional.of(collection));
    when(collectionRepository.existsByNameAndStoreIdAndIdNot("Verano", 1, 1)).thenReturn(false);
    when(productRepository.findByIdIn(Set.of(10))).thenReturn(List.of(product));
    when(collectionRepository.save(any(ProductCollection.class))).thenReturn(updated);

    CollectionResponseDTO result = collectionService.update(1, dto);

    assertEquals("Verano", result.getName());
    assertEquals(List.of(10), result.getProductIds());
  }

  @Test
  void testUpdate_conflict() {
    CollectionUpdateDTO dto = new CollectionUpdateDTO();
    dto.setName("Verano");
    dto.setProductIds(Set.of(10));

    when(collectionRepository.findById(1)).thenReturn(Optional.of(collection));
    when(collectionRepository.existsByNameAndStoreIdAndIdNot("Verano", 1, 1)).thenReturn(true);

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> collectionService.update(1, dto));

    assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
  }

  @Test
  void testUpdate_notFound() {
    CollectionUpdateDTO dto = new CollectionUpdateDTO();
    dto.setName("Verano");
    dto.setProductIds(Set.of());

    when(collectionRepository.findById(99)).thenReturn(Optional.empty());

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> collectionService.update(99, dto));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
  }

  @Test
  void testDelete_ok() {
    when(collectionRepository.findById(1)).thenReturn(Optional.of(collection));

    collectionService.delete(1);

    verify(collectionRepository).delete(collection);
  }

  @Test
  void testDelete_notFound() {
    when(collectionRepository.findById(99)).thenReturn(Optional.empty());

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> collectionService.delete(99));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
  }
}
