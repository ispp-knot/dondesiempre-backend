package ispp.project.dondesiempre.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import ispp.project.dondesiempre.dto.category.CategoryCreationDTO;
import ispp.project.dondesiempre.dto.category.CategoryResponseDTO;
import ispp.project.dondesiempre.dto.category.CategoryUpdateDTO;
import ispp.project.dondesiempre.models.products.ProductCategory;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.services.CategoryService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private CategoryService categoryService;

  private CategoryResponseDTO categoryResponse;

  @BeforeEach
  void setUp() {
    Store store = new Store();
    store.setId(1);
    store.setName("Tienda Test");

    ProductCategory category = new ProductCategory();
    category.setId(1);
    category.setName("Camisetas");
    category.setDescription("Ropa de verano");
    category.setStore(store);

    categoryResponse = new CategoryResponseDTO(category);
  }

  @Test
  void testGetByStore() throws Exception {
    when(categoryService.getByStore(1)).thenReturn(List.of(categoryResponse));

    mockMvc
        .perform(get("/api/v1/store/1/categories"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Camisetas"))
        .andExpect(jsonPath("$[0].storeId").value(1));
  }

  @Test
  void testGetById_found() throws Exception {
    when(categoryService.getById(1)).thenReturn(categoryResponse);

    mockMvc
        .perform(get("/api/v1/categories/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Camisetas"));
  }

  @Test
  void testGetById_notFound() throws Exception {
    when(categoryService.getById(anyInt()))
        .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    mockMvc.perform(get("/api/v1/categories/99")).andExpect(status().isNotFound());
  }

  @Test
  void testCreate_ok() throws Exception {
    CategoryCreationDTO dto = new CategoryCreationDTO();
    dto.setName("Camisetas");
    dto.setDescription("Ropa de verano");

    when(categoryService.create(eq(1), any(CategoryCreationDTO.class)))
        .thenReturn(categoryResponse);

    mockMvc
        .perform(
            post("/api/v1/store/1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Camisetas"));
  }

  @Test
  void testCreate_storeNotFound() throws Exception {
    CategoryCreationDTO dto = new CategoryCreationDTO();
    dto.setName("Camisetas");

    when(categoryService.create(eq(99), any(CategoryCreationDTO.class)))
        .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    mockMvc
        .perform(
            post("/api/v1/store/99/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isNotFound());
  }

  @Test
  void testCreate_conflict() throws Exception {
    CategoryCreationDTO dto = new CategoryCreationDTO();
    dto.setName("Camisetas");

    when(categoryService.create(eq(1), any(CategoryCreationDTO.class)))
        .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT));

    mockMvc
        .perform(
            post("/api/v1/store/1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isConflict());
  }

  @Test
  void testUpdate_ok() throws Exception {
    CategoryUpdateDTO dto = new CategoryUpdateDTO();
    dto.setName("Zapatillas");

    CategoryResponseDTO updated = new CategoryResponseDTO();
    updated.setId(1);
    updated.setName("Zapatillas");
    updated.setStoreId(1);
    updated.setStoreName("Tienda Test");

    when(categoryService.update(eq(1), any(CategoryUpdateDTO.class))).thenReturn(updated);

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
    CategoryUpdateDTO dto = new CategoryUpdateDTO();
    dto.setName("Zapatillas");

    when(categoryService.update(eq(99), any(CategoryUpdateDTO.class)))
        .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    mockMvc
        .perform(
            put("/api/v1/categories/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isNotFound());
  }

  @Test
  void testDelete_ok() throws Exception {
    doNothing().when(categoryService).delete(1);

    mockMvc.perform(delete("/api/v1/categories/1")).andExpect(status().isNoContent());
  }

  @Test
  void testDelete_notFound() throws Exception {
    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(categoryService).delete(99);

    mockMvc.perform(delete("/api/v1/categories/99")).andExpect(status().isNotFound());
  }

  @Test
  void testGetByStore_empty() throws Exception {
    when(categoryService.getByStore(anyInt())).thenReturn(List.of());

    mockMvc
        .perform(get("/api/v1/store/99/categories"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isEmpty());
  }

  @Test
  void testCreate_sameNameDifferentStore() throws Exception {
    CategoryCreationDTO dto = new CategoryCreationDTO();
    dto.setName("Camisetas");

    CategoryResponseDTO categoryStore2 = new CategoryResponseDTO();
    categoryStore2.setId(2);
    categoryStore2.setName("Camisetas");
    categoryStore2.setStoreId(2);
    categoryStore2.setStoreName("Tienda Test 2");

    when(categoryService.create(eq(2), any(CategoryCreationDTO.class))).thenReturn(categoryStore2);

    mockMvc
        .perform(
            post("/api/v1/store/2/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Camisetas"));
  }
}
