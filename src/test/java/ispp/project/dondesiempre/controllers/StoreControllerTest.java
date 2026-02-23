package ispp.project.dondesiempre.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ispp.project.dondesiempre.controllers.exceptions.GlobalExceptionHandler;
import ispp.project.dondesiempre.controllers.stores.StoreController;
import ispp.project.dondesiempre.models.stores.dto.StoresBoundingBoxDTO;
import ispp.project.dondesiempre.services.stores.StoreService;
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

  @lombok.Getter
  @lombok.Builder
  static class MockStoreDTO implements StoresBoundingBoxDTO {
    private Integer id;
    private String name;
    private String email;
    private String storeID;
    private String address;
    private String openingHours;
    private String phone;
    private Boolean acceptsShipping;
    private Double latitude;
    private Double longitude;
  }

  @Test
  void shouldReturnOkAndListOfStores_whenValidParamsProvided() throws Exception {
    // Dado
    double minLon = -6.0, minLat = 37.0, maxLon = -5.0, maxLat = 38.0;

    StoresBoundingBoxDTO mockDto =
        MockStoreDTO.builder().name("Tienda Centro").latitude(37.5).longitude(-5.5).build();

    when(storeService.findStoresInBoundingBox(minLon, minLat, maxLon, maxLat))
        .thenReturn(List.of(mockDto));

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
}
