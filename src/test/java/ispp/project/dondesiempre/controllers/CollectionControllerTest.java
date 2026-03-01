package ispp.project.dondesiempre.controllers;

import static org.mockito.ArgumentMatchers.any;
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
import java.util.UUID;
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

  private static final UUID STORE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final UUID STORE_2_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
  private static final UUID COLLECTION_ID = UUID.fromString("00000000-0000-0000-0000-000000000010");
  private static final UUID PRODUCT_ID = UUID.fromString("00000000-0000-0000-0000-000000000100");

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private CollectionService collectionService;

  private CollectionResponseDTO collectionResponse;

  @BeforeEach
  void setUp() {
    Store store = new Store();
    store.setId(STORE_ID);
    store.setName("Tienda Test");

    Product product = new Product();
    product.setId(PRODUCT_ID);
    product.setStore(store);

    ProductCollection collection = new ProductCollection();
    collection.setId(COLLECTION_ID);
    collection.setName("Primavera");
    collection.setDescription("Coleccion de primavera");
    collection.setStore(store);
    collection.setProducts(Set.of(product));

    collectionResponse = new CollectionResponseDTO(collection);
  }

  @Test
  void testGetByStore() throws Exception {
    when(collectionService.getByStore(STORE_ID)).thenReturn(List.of(collectionResponse));

    mockMvc
        .perform(get("/api/v1/store/{storeId}/collections", STORE_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Primavera"))
        .andExpect(jsonPath("$[0].productIds[0]").value(PRODUCT_ID.toString()));
  }

  @Test
  void testGetById_found() throws Exception {
    when(collectionService.getById(COLLECTION_ID)).thenReturn(collectionResponse);

    mockMvc
        .perform(get("/api/v1/collections/{id}", COLLECTION_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Primavera"));
  }

  @Test
  void testGetById_notFound() throws Exception {
    UUID randomId = UUID.randomUUID();
    when(collectionService.getById(randomId))
        .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    mockMvc.perform(get("/api/v1/collections/{id}", randomId)).andExpect(status().isNotFound());
  }

  @Test
  void testCreate_ok() throws Exception {
    CollectionCreationDTO dto = new CollectionCreationDTO();
    dto.setName("Primavera");
    dto.setDescription("Coleccion de primavera");
    dto.setProductIds(Set.of(PRODUCT_ID));

    when(collectionService.create(eq(STORE_ID), any(CollectionCreationDTO.class)))
        .thenReturn(collectionResponse);

    mockMvc
        .perform(
            post("/api/v1/store/{storeId}/collections", STORE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Primavera"))
        .andExpect(jsonPath("$.productIds[0]").value(PRODUCT_ID.toString()));
  }

  @Test
  void testCreate_storeNotFound() throws Exception {
    UUID randomStoreId = UUID.randomUUID();
    CollectionCreationDTO dto = new CollectionCreationDTO();
    dto.setName("Primavera");
    dto.setProductIds(Set.of());

    when(collectionService.create(eq(randomStoreId), any(CollectionCreationDTO.class)))
        .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    mockMvc
        .perform(
            post("/api/v1/store/{storeId}/collections", randomStoreId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isNotFound());
  }

  @Test
  void testCreate_conflict() throws Exception {
    CollectionCreationDTO dto = new CollectionCreationDTO();
    dto.setName("Primavera");
    dto.setProductIds(Set.of());

    when(collectionService.create(eq(STORE_ID), any(CollectionCreationDTO.class)))
        .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT));

    mockMvc
        .perform(
            post("/api/v1/store/{storeId}/collections", STORE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isConflict());
  }

  @Test
  void testUpdate_ok() throws Exception {
    CollectionUpdateDTO dto = new CollectionUpdateDTO();
    dto.setName("Verano");
    dto.setProductIds(Set.of(PRODUCT_ID));

    CollectionResponseDTO updated = new CollectionResponseDTO();
    updated.setId(COLLECTION_ID);
    updated.setName("Verano");
    updated.setStoreId(STORE_ID);
    updated.setStoreName("Tienda Test");
    updated.setProductIds(List.of(PRODUCT_ID));

    when(collectionService.update(eq(COLLECTION_ID), any(CollectionUpdateDTO.class)))
        .thenReturn(updated);

    mockMvc
        .perform(
            put("/api/v1/collections/{id}", COLLECTION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Verano"))
        .andExpect(jsonPath("$.productIds[0]").value(PRODUCT_ID.toString()));
  }

  @Test
  void testUpdate_notFound() throws Exception {
    UUID randomId = UUID.randomUUID();
    CollectionUpdateDTO dto = new CollectionUpdateDTO();
    dto.setName("Verano");
    dto.setProductIds(Set.of());

    when(collectionService.update(eq(randomId), any(CollectionUpdateDTO.class)))
        .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    mockMvc
        .perform(
            put("/api/v1/collections/{id}", randomId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isNotFound());
  }

  @Test
  void testDelete_ok() throws Exception {
    doNothing().when(collectionService).delete(COLLECTION_ID);

    mockMvc
        .perform(delete("/api/v1/collections/{id}", COLLECTION_ID))
        .andExpect(status().isNoContent());
  }

  @Test
  void testDelete_notFound() throws Exception {
    UUID randomId = UUID.randomUUID();
    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
        .when(collectionService)
        .delete(randomId);

    mockMvc.perform(delete("/api/v1/collections/{id}", randomId)).andExpect(status().isNotFound());
  }

  @Test
  void testGetByStore_empty() throws Exception {
    when(collectionService.getByStore(STORE_2_ID)).thenReturn(List.of());

    mockMvc
        .perform(get("/api/v1/store/{storeId}/collections", STORE_2_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isEmpty());
  }
}
