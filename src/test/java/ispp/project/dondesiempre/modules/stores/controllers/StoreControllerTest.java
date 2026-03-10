package ispp.project.dondesiempre.modules.stores.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ispp.project.dondesiempre.config.GlobalExceptionHandler;
import ispp.project.dondesiempre.mockEntities.StoreMockEntities;
import ispp.project.dondesiempre.modules.stores.dtos.StoreDTO;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.services.StoreService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
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

  private static final UUID TEST_STORE_ID = UUID.randomUUID();
  private static final Store TEST_STORE = StoreMockEntities.sampleStore(TEST_STORE_ID);

  @Test
  void shouldReturnOkAndListOfStores_whenValidParamsProvided() throws Exception {
    // Dado
    double minLon = -6.0, minLat = 37.0, maxLon = -5.0, maxLat = 38.0;

    StoreDTO storeDTO = new StoreDTO();
    storeDTO.setName("Tienda Centro");
    storeDTO.setLatitude(37.5);
    storeDTO.setLongitude(-5.5);

    when(storeService.findStoresInBoundingBox(minLon, minLat, maxLon, maxLat))
        .thenReturn(List.of(TEST_STORE));
    when(storeService.toDTO(TEST_STORE)).thenReturn(storeDTO);

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

  /*
   * @Test
   * void shouldReturnOkAndStoreDTO_whenGetStoreByIdExists() throws Exception {
   * UUID storeId = UUID.randomUUID();
   * Store store = StoreMockEntities.sampleStore(storeId);
   * StoreDTO storeDTO = new StoreDTO();
   * storeDTO.setName("La Boutique");
   * storeDTO.setAddress("Calle Mayor 1");
   *
   * when(storeService.findById(storeId)).thenReturn(store);
   * when(storeService.toDTO(store)).thenReturn(storeDTO);
   *
   * mockMvc
   * .perform(get("/api/v1/stores/{id}", storeId))
   * .andExpect(status().isOk())
   * .andExpect(jsonPath("$.name").value("La Boutique"))
   * .andExpect(jsonPath("$.address").value("Calle Mayor 1"));
   * }
   *
   * @Test
   * void followStore_shouldReturnForbidden_whenNotAuthorized() throws Exception {
   * mockMvc
   * .perform(post("/api/v1/stores/{storeId}/followers", TEST_STORE_ID))
   * .andExpect(status().is(403));
   * }
   */
}
