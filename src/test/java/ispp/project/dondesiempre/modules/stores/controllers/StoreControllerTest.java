package ispp.project.dondesiempre.modules.stores.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ispp.project.dondesiempre.config.GlobalExceptionHandler;
import ispp.project.dondesiempre.modules.stores.dtos.StoreDTO;
import ispp.project.dondesiempre.modules.stores.services.StoreService;
import java.util.List;
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
