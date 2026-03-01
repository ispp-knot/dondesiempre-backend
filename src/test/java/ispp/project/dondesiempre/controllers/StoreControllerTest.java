package ispp.project.dondesiempre.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import ispp.project.dondesiempre.controllers.exceptions.GlobalExceptionHandler;
import ispp.project.dondesiempre.controllers.stores.StoreController;
import ispp.project.dondesiempre.models.stores.dto.StoreDTO;
import ispp.project.dondesiempre.models.stores.dto.StoreUpdateDTO;
import ispp.project.dondesiempre.services.stores.StoreService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    controllers = StoreController.class,
    excludeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {GlobalExceptionHandler.class}))
public class StoreControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private StoreService storeService;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void shouldReturnOkAndListOfStores_whenValidParamsProvided() throws Exception {
    // Dado
    double minLon = -6.0, minLat = 37.0, maxLon = -5.0, maxLat = 38.0;

    StoreDTO store = new StoreDTO();
    store.setName("Tienda Centro");
    store.setLatitude(37.5);
    store.setLongitude(-5.5);

    when(storeService.findStoresInBoundingBox(minLon, minLat, maxLon, maxLat))
        .thenReturn(List.of(store));

    mockMvc
        .perform(
            get("/api/v1/stores")
                .param("minLon", String.valueOf(minLon))
                .param("minLat", String.valueOf(minLat))
                .param("maxLon", String.valueOf(maxLon))
                .param("maxLat", String.valueOf(maxLat)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1))
        .andExpect(jsonPath("$[0].name").value("Tienda Centro"))
        .andExpect(jsonPath("$[0].latitude").value(37.5))
        .andExpect(jsonPath("$[0].longitude").value(-5.5));
  }

  @Test
  void shouldReturnBadRequest_whenRequiredParamsAreMissing() throws Exception {
    mockMvc
        .perform(get("/api/v1/stores").param("minLon", "-6.0"))
        .andExpect(status().isBadRequest());
  }

  @Test
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
}
