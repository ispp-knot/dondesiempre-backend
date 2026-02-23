package ispp.project.dondesiempre.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import ispp.project.dondesiempre.dto.category.CategoryRequestDTO;
import ispp.project.dondesiempre.models.products.Category;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.repositories.products.CategoryRepository;
import ispp.project.dondesiempre.repositories.stores.StoreRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private CategoryRepository categoryRepository;

  @MockitoBean private StoreRepository storeRepository;

  private Store store;
  private Category category;

  @BeforeEach
  void setUp() {
    store = new Store();
    store.setId(1);
    store.setName("Tienda Test");

    category = new Category();
    category.setId(1);
    category.setName("Camisetas");
    category.setDescription("Ropa de verano");
    category.setStore(store);
  }

  @Test
  void testGetByStore() throws Exception {
    when(categoryRepository.findByStoreId(1)).thenReturn(List.of(category));

    mockMvc
        .perform(get("/api/v1/categories/store/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Camisetas"))
        .andExpect(jsonPath("$[0].storeId").value(1));
  }

  @Test
  void testGetById_found() throws Exception {
    when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

    mockMvc
        .perform(get("/api/v1/categories/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Camisetas"));
  }

  @Test
  void testGetById_notFound() throws Exception {
    when(categoryRepository.findById(anyInt())).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/v1/categories/99")).andExpect(status().isNotFound());
  }

  @Test
  void testCreate_ok() throws Exception {
    CategoryRequestDTO dto = new CategoryRequestDTO();
    dto.setName("Camisetas");
    dto.setDescription("Ropa de verano");
    dto.setStoreId(1);

    when(storeRepository.findById(1)).thenReturn(Optional.of(store));
    when(categoryRepository.existsByNameAndStoreId(anyString(), anyInt())).thenReturn(false);
    when(categoryRepository.save(any())).thenReturn(category);

    mockMvc
        .perform(
            post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Camisetas"));
  }

  @Test
  void testCreate_storeNotFound() throws Exception {
    CategoryRequestDTO dto = new CategoryRequestDTO();
    dto.setName("Camisetas");
    dto.setStoreId(99);

    when(storeRepository.findById(99)).thenReturn(Optional.empty());

    mockMvc
        .perform(
            post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testCreate_conflict() throws Exception {
    CategoryRequestDTO dto = new CategoryRequestDTO();
    dto.setName("Camisetas");
    dto.setStoreId(1);

    when(storeRepository.findById(1)).thenReturn(Optional.of(store));
    when(categoryRepository.existsByNameAndStoreId("Camisetas", 1)).thenReturn(true);

    mockMvc
        .perform(
            post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isConflict());
  }

  @Test
  void testUpdate_ok() throws Exception {
    CategoryRequestDTO dto = new CategoryRequestDTO();
    dto.setName("Zapatillas");
    dto.setStoreId(1);

    Category updated = new Category();
    updated.setId(1);
    updated.setName("Zapatillas");
    updated.setStore(store);

    when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
    when(categoryRepository.save(any())).thenReturn(updated);

    mockMvc
        .perform(
            put("/api/v1/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Zapatillas"));
  }

  @Test
  void testUpdate_notFound() throws Exception {
    CategoryRequestDTO dto = new CategoryRequestDTO();
    dto.setName("Zapatillas");
    dto.setStoreId(1);

    when(categoryRepository.findById(anyInt())).thenReturn(Optional.empty());

    mockMvc
        .perform(
            put("/api/v1/categories/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isNotFound());
  }

  @Test
  void testDelete_ok() throws Exception {
    when(categoryRepository.existsById(1)).thenReturn(true);

    mockMvc.perform(delete("/api/v1/categories/1")).andExpect(status().isNoContent());
  }

  @Test
  void testDelete_notFound() throws Exception {
    when(categoryRepository.existsById(anyInt())).thenReturn(false);

    mockMvc.perform(delete("/api/v1/categories/99")).andExpect(status().isNotFound());
  }

  @Test
  void testGetByStore_empty() throws Exception {
    when(categoryRepository.findByStoreId(anyInt())).thenReturn(List.of());

    mockMvc
        .perform(get("/api/v1/categories/store/99"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isEmpty());
  }

  @Test
  void testCreate_sameNameDifferentStore() throws Exception {
    Store store2 = new Store();
    store2.setId(2);
    store2.setName("Tienda Test 2");

    CategoryRequestDTO dto = new CategoryRequestDTO();
    dto.setName("Camisetas");
    dto.setStoreId(2);

    Category cat2 = new Category();
    cat2.setId(2);
    cat2.setName("Camisetas");
    cat2.setStore(store2);

    when(storeRepository.findById(2)).thenReturn(Optional.of(store2));
    when(categoryRepository.existsByNameAndStoreId("Camisetas", 2)).thenReturn(false);
    when(categoryRepository.save(any())).thenReturn(cat2);

    mockMvc
        .perform(
            post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Camisetas"));
  }
}
