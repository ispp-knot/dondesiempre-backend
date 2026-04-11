package ispp.project.dondesiempre.modules.stores.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import ispp.project.dondesiempre.config.GlobalExceptionHandler;
import ispp.project.dondesiempre.config.security.SecurityConfig;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.payment.services.PaymentService;
import ispp.project.dondesiempre.modules.stores.dtos.StoreDTO;
import ispp.project.dondesiempre.modules.stores.dtos.StoreUpdateDTO;
import ispp.project.dondesiempre.modules.stores.dtos.StoreUpdateLocationDTO;
import ispp.project.dondesiempre.modules.stores.services.StoreService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    controllers = StoreController.class,
    excludeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {GlobalExceptionHandler.class}))
@Import(SecurityConfig.class)
public class StoreControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private StoreService storeService;
  @MockitoBean private PaymentService paymentService;

  private static final java.util.UUID TEST_STORE_ID = java.util.UUID.randomUUID();
  private static final ispp.project.dondesiempre.modules.stores.models.Store TEST_STORE =
      ispp.project.dondesiempre.mockEntities.StoreMockEntities.sampleStore(TEST_STORE_ID);

  @Test
  void shouldReturnOkAndListOfStores_whenSearchingByName() throws Exception {
    // Dado
    String name = "Tienda";
    Double lat = 37.5;
    Double lon = -5.5;

    StoreDTO storeDTO = new StoreDTO();
    storeDTO.setName("Tienda Centro");

    when(storeService.searchStores(name, lat, lon)).thenReturn(List.of(TEST_STORE));
    when(storeService.toDTO(TEST_STORE, lat, lon)).thenReturn(storeDTO);

    mockMvc
        .perform(
            get("/api/v1/stores")
                .param("name", name)
                .param("lat", String.valueOf(lat))
                .param("lon", String.valueOf(lon)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1))
        .andExpect(jsonPath("$[0].name").value("Tienda Centro"));
  }

  @Test
  void shouldReturnOkAndListOfStores_whenQueryingMap() throws Exception {
    // Dado
    double minLon = -6.0, minLat = 37.0, maxLon = -5.0, maxLat = 38.0;

    StoreDTO storeDTO = new StoreDTO();
    storeDTO.setName("Tienda Centro");

    when(storeService.findStoresInBoundingBoxAsDTO(minLon, minLat, maxLon, maxLat))
        .thenReturn(List.of(storeDTO));

    mockMvc
        .perform(
            get("/api/v1/stores/map")
                .param("minLon", String.valueOf(minLon))
                .param("minLat", String.valueOf(minLat))
                .param("maxLon", String.valueOf(maxLon))
                .param("maxLat", String.valueOf(maxLat)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1))
        .andExpect(jsonPath("$[0].name").value("Tienda Centro"));
  }

  @Test
  @WithMockUser
  void shouldReturnStoreDTO_whenUpdateStoreIsSuccessful() throws Exception {
    UUID id = UUID.randomUUID();
    StoreUpdateDTO updateDTO = new StoreUpdateDTO();
    updateDTO.setName("Nombre Actualizado");
    updateDTO.setEmail("nuevo@ejemplo.es");

    StoreDTO responseDTO = new StoreDTO();
    responseDTO.setName("Nombre Actualizado");
    responseDTO.setEmail("nuevo@ejemplo.es");

    when(storeService.updateStore(eq(id), any(StoreUpdateDTO.class))).thenReturn(responseDTO);

    mockMvc
        .perform(
            put("/api/v1/stores/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Nombre Actualizado"))
        .andExpect(jsonPath("$.email").value("nuevo@ejemplo.es"));

    verify(storeService, times(1)).updateStore(eq(id), any(StoreUpdateDTO.class));
  }

  @Test
  @WithMockUser()
  void updateStore_shouldReturnOk_whenUserIsOwner() throws Exception {

    UUID storeId = UUID.randomUUID();

    StoreUpdateDTO dto = new StoreUpdateDTO();
    dto.setName("Updated Store");

    StoreDTO storeDTO = new StoreDTO();
    storeDTO.setId(storeId);
    storeDTO.setName("Updated Store");

    when(storeService.updateStore(eq(storeId), any())).thenReturn(storeDTO);

    mockMvc
        .perform(
            put("/api/v1/stores/" + storeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(storeId.toString()))
        .andExpect(jsonPath("$.name").value("Updated Store"));
  }

  @Test
  @WithMockUser()
  void updateStore_shouldReturnForbidden_whenUserIsNotOwner() throws Exception {

    UUID storeId = UUID.randomUUID();

    StoreUpdateDTO dto = new StoreUpdateDTO();
    dto.setName("Updated Store");

    when(storeService.updateStore(eq(storeId), any()))
        .thenThrow(new org.springframework.security.access.AccessDeniedException("Not owner"));

    mockMvc
        .perform(
            put("/api/v1/stores/" + storeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(username = "owner@test.com")
  void updateStore_shouldReturnBadRequest_whenInvalidEmail() throws Exception {

    UUID storeId = UUID.randomUUID();

    StoreUpdateDTO dto = new StoreUpdateDTO();
    dto.setEmail("invalid-email");

    mockMvc
        .perform(
            put("/api/v1/stores/" + storeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "owner@test.com")
  void updateStore_shouldReturnNotFound_whenStoreDoesNotExist() throws Exception {

    UUID storeId = UUID.randomUUID();

    StoreUpdateDTO dto = new StoreUpdateDTO();
    dto.setName("Updated Store");

    when(storeService.updateStore(eq(storeId), any(StoreUpdateDTO.class)))
        .thenThrow(new ResourceNotFoundException("Store not found"));

    mockMvc
        .perform(
            put("/api/v1/stores/" + storeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isNotFound());
  }

  // @Test
  // void shouldReturnOkAndStoreDTO_whenGetStoreByIdExists() throws Exception {
  // UUID storeId = UUID.randomUUID();
  // Store store = StoreMockEntities.sampleStore(storeId);
  // StoreDTO storeDTO = new StoreDTO();
  // storeDTO.setName("La Boutique");
  // storeDTO.setAddress("Calle Mayor 1");

  // when(storeService.findById(storeId)).thenReturn(store);
  // when(storeService.toDTO(store)).thenReturn(storeDTO);

  // mockMvc
  // .perform(get("/api/v1/stores/{id}", storeId))
  // .andExpect(status().isOk())
  // .andExpect(jsonPath("$.name").value("La Boutique"))
  // .andExpect(jsonPath("$.address").value("Calle Mayor 1"));
  // }

  // @Test
  // void followStore_shouldReturnForbidden_whenNotAuthorized() throws Exception {
  // mockMvc
  // .perform(post("/api/v1/stores/{storeId}/followers", TEST_STORE_ID))
  // .andExpect(status().is(403));
  // }

  @Test
  @WithMockUser
  void updateStoreLocation_shouldReturnOk_whenValidRequest() throws Exception {
    UUID storeId = UUID.randomUUID();
    StoreUpdateLocationDTO dto = new StoreUpdateLocationDTO();
    dto.setLongitude(-5.9281);
    dto.setLatitude(37.2829);

    StoreDTO responseDTO = new StoreDTO();
    responseDTO.setId(storeId);
    responseDTO.setName("Store with new location");

    when(storeService.updateLocation(eq(storeId), eq(-5.9281), eq(37.2829)))
        .thenReturn(responseDTO);

    mockMvc
        .perform(
            put("/api/v1/stores/" + storeId + "/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(storeId.toString()))
        .andExpect(jsonPath("$.name").value("Store with new location"));

    verify(storeService, times(1)).updateLocation(eq(storeId), eq(-5.9281), eq(37.2829));
  }

  @Test
  @WithMockUser
  void updateStoreLocation_shouldReturnForbidden_whenUserIsNotOwner() throws Exception {
    UUID storeId = UUID.randomUUID();
    StoreUpdateLocationDTO dto = new StoreUpdateLocationDTO();
    dto.setLongitude(-5.9281);
    dto.setLatitude(37.2829);

    when(storeService.updateLocation(eq(storeId), any(), any()))
        .thenThrow(new org.springframework.security.access.AccessDeniedException("Not owner"));

    mockMvc
        .perform(
            put("/api/v1/stores/" + storeId + "/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser
  void updateStoreLocation_shouldReturnBadRequest_whenMissingCoordinates() throws Exception {
    UUID storeId = UUID.randomUUID();

    StoreUpdateLocationDTO emptyDto = new StoreUpdateLocationDTO();

    mockMvc
        .perform(
            put("/api/v1/stores/" + storeId + "/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyDto)))
        .andExpect(status().isBadRequest());

    verify(storeService, never()).updateLocation(any(), any(), any());
  }
}
