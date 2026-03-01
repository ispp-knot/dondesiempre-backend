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
import ispp.project.dondesiempre.dto.collection.CollectionCreationDTO;
import ispp.project.dondesiempre.dto.collection.CollectionResponseDTO;
import ispp.project.dondesiempre.dto.collection.CollectionUpdateDTO;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.models.products.ProductCollection;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.services.CollectionService;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(CollectionController.class)
class CollectionControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private CollectionService collectionService;

  private CollectionResponseDTO collectionResponse;

  @BeforeEach
  void setUp() {
    Store store = new Store();
    store.setId(1);
    store.setName("Tienda Test");

    Product product = new Product();
    product.setId(10);
    product.setStore(store);

    ProductCollection collection = new ProductCollection();
    collection.setId(1);
    collection.setName("Primavera");
    collection.setDescription("Coleccion de primavera");
    collection.setStore(store);
    collection.setProducts(Set.of(product));

    collectionResponse = new CollectionResponseDTO(collection);
  }

  @Test
  void testGetByStore() throws Exception {
    when(collectionService.getByStore(1)).thenReturn(List.of(collectionResponse));

    mockMvc
        .perform(get("/api/v1/store/1/collections"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Primavera"))
        .andExpect(jsonPath("$[0].productIds[0]").value(10));
  }

  @Test
  void testGetById_found() throws Exception {
    when(collectionService.getById(1)).thenReturn(collectionResponse);

    mockMvc
        .perform(get("/api/v1/collections/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Primavera"));
  }

  @Test
  void testGetById_notFound() throws Exception {
    when(collectionService.getById(anyInt()))
        .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    mockMvc.perform(get("/api/v1/collections/99")).andExpect(status().isNotFound());
  }

  @Test
  void testCreate_ok() throws Exception {
    CollectionCreationDTO dto = new CollectionCreationDTO();
    dto.setName("Primavera");
    dto.setDescription("Coleccion de primavera");
    dto.setProductIds(Set.of(10));

    when(collectionService.create(eq(1), any(CollectionCreationDTO.class)))
        .thenReturn(collectionResponse);

    mockMvc
        .perform(
            post("/api/v1/store/1/collections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Primavera"))
        .andExpect(jsonPath("$.productIds[0]").value(10));
  }

  @Test
  void testCreate_storeNotFound() throws Exception {
    CollectionCreationDTO dto = new CollectionCreationDTO();
    dto.setName("Primavera");
    dto.setProductIds(Set.of());

    when(collectionService.create(eq(99), any(CollectionCreationDTO.class)))
        .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    mockMvc
        .perform(
            post("/api/v1/store/99/collections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isNotFound());
  }

  @Test
  void testCreate_conflict() throws Exception {
    CollectionCreationDTO dto = new CollectionCreationDTO();
    dto.setName("Primavera");
    dto.setProductIds(Set.of());

    when(collectionService.create(eq(1), any(CollectionCreationDTO.class)))
        .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT));

    mockMvc
        .perform(
            post("/api/v1/store/1/collections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isConflict());
  }

  @Test
  void testUpdate_ok() throws Exception {
    CollectionUpdateDTO dto = new CollectionUpdateDTO();
    dto.setName("Verano");
    dto.setProductIds(Set.of(10));

    CollectionResponseDTO updated = new CollectionResponseDTO();
    updated.setId(1);
    updated.setName("Verano");
    updated.setStoreId(1);
    updated.setStoreName("Tienda Test");
    updated.setProductIds(List.of(10));

    when(collectionService.update(eq(1), any(CollectionUpdateDTO.class))).thenReturn(updated);

    mockMvc
        .perform(
            put("/api/v1/collections/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Verano"))
        .andExpect(jsonPath("$.productIds[0]").value(10));
  }

  @Test
  void testUpdate_notFound() throws Exception {
    CollectionUpdateDTO dto = new CollectionUpdateDTO();
    dto.setName("Verano");
    dto.setProductIds(Set.of());

    when(collectionService.update(eq(99), any(CollectionUpdateDTO.class)))
        .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    mockMvc
        .perform(
            put("/api/v1/collections/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isNotFound());
  }

  @Test
  void testDelete_ok() throws Exception {
    doNothing().when(collectionService).delete(1);

    mockMvc.perform(delete("/api/v1/collections/1")).andExpect(status().isNoContent());
  }

  @Test
  void testDelete_notFound() throws Exception {
    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(collectionService).delete(99);

    mockMvc.perform(delete("/api/v1/collections/99")).andExpect(status().isNotFound());
  }

  @Test
  void testGetByStore_empty() throws Exception {
    when(collectionService.getByStore(anyInt())).thenReturn(List.of());

    mockMvc
        .perform(get("/api/v1/store/99/collections"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isEmpty());
  }
}
