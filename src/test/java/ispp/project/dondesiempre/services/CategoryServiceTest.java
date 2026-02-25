package ispp.project.dondesiempre.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ispp.project.dondesiempre.dto.category.CategoryCreationDTO;
import ispp.project.dondesiempre.dto.category.CategoryResponseDTO;
import ispp.project.dondesiempre.dto.category.CategoryUpdateDTO;
import ispp.project.dondesiempre.models.products.ProductCategory;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.repositories.products.CategoryRepository;
import ispp.project.dondesiempre.repositories.stores.StoreRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  @Mock private CategoryRepository categoryRepository;

  @Mock private StoreRepository storeRepository;

  @InjectMocks private CategoryService categoryService;

  private Store store;
  private ProductCategory category;

  @BeforeEach
  void setUp() {
    store = new Store();
    store.setId(1);
    store.setName("Tienda Test");

    category = new ProductCategory();
    category.setId(1);
    category.setName("Camisetas");
    category.setDescription("Ropa de verano");
    category.setStore(store);
  }

  @Test
  void testGetByStore() {
    when(categoryRepository.findByStoreId(1)).thenReturn(List.of(category));

    List<CategoryResponseDTO> result = categoryService.getByStore(1);

    assertEquals(1, result.size());
    assertEquals("Camisetas", result.getFirst().getName());
  }

  @Test
  void testGetById_found() {
    when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

    CategoryResponseDTO result = categoryService.getById(1);

    assertEquals(1, result.getId());
    assertEquals("Camisetas", result.getName());
  }

  @Test
  void testGetById_notFound() {
    when(categoryRepository.findById(99)).thenReturn(Optional.empty());

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> categoryService.getById(99));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
  }

  @Test
  void testCreate_ok() {
    CategoryCreationDTO dto = new CategoryCreationDTO();
    dto.setName("Camisetas");
    dto.setDescription("Ropa de verano");

    when(storeRepository.findById(1)).thenReturn(Optional.of(store));
    when(categoryRepository.existsByNameAndStoreId("Camisetas", 1)).thenReturn(false);
    when(categoryRepository.save(any(ProductCategory.class))).thenReturn(category);

    CategoryResponseDTO result = categoryService.create(1, dto);

    assertEquals("Camisetas", result.getName());
    assertEquals(1, result.getStoreId());
  }

  @Test
  void testCreate_storeNotFound() {
    CategoryCreationDTO dto = new CategoryCreationDTO();
    dto.setName("Camisetas");

    when(storeRepository.findById(99)).thenReturn(Optional.empty());

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> categoryService.create(99, dto));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    verify(categoryRepository, never()).save(any(ProductCategory.class));
  }

  @Test
  void testCreate_conflict() {
    CategoryCreationDTO dto = new CategoryCreationDTO();
    dto.setName("Camisetas");

    when(storeRepository.findById(1)).thenReturn(Optional.of(store));
    when(categoryRepository.existsByNameAndStoreId("Camisetas", 1)).thenReturn(true);

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> categoryService.create(1, dto));

    assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    verify(categoryRepository, never()).save(any(ProductCategory.class));
  }

  @Test
  void testUpdate_ok() {
    CategoryUpdateDTO dto = new CategoryUpdateDTO();
    dto.setName("Zapatillas");
    dto.setDescription("Calzado");

    ProductCategory updated = new ProductCategory();
    updated.setId(1);
    updated.setName("Zapatillas");
    updated.setDescription("Calzado");
    updated.setStore(store);

    when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
    when(categoryRepository.save(any(ProductCategory.class))).thenReturn(updated);

    CategoryResponseDTO result = categoryService.update(1, dto);

    assertEquals("Zapatillas", result.getName());
    assertEquals("Calzado", result.getDescription());
  }

  @Test
  void testUpdate_notFound() {
    CategoryUpdateDTO dto = new CategoryUpdateDTO();
    dto.setName("Zapatillas");

    when(categoryRepository.findById(99)).thenReturn(Optional.empty());

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> categoryService.update(99, dto));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
  }

  @Test
  void testDelete_ok() {
    when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

    categoryService.delete(1);

    verify(categoryRepository).delete(category);
  }

  @Test
  void testDelete_notFound() {
    when(categoryRepository.findById(99)).thenReturn(Optional.empty());

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> categoryService.delete(99));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
  }
}
