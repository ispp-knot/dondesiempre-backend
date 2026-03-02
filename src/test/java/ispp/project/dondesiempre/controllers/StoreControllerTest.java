package ispp.project.dondesiempre.controllers;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ispp.project.dondesiempre.controllers.exceptions.GlobalExceptionHandler;
import ispp.project.dondesiempre.controllers.stores.StoreController;
import ispp.project.dondesiempre.mockEntities.StoreMockEntities;
import ispp.project.dondesiempre.models.Client;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.models.stores.StoreFollower;
import ispp.project.dondesiempre.models.stores.dto.StoreDTO;
import ispp.project.dondesiempre.services.stores.StoreService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
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

  private static final Client TEST_CLIENT = StoreMockEntities.sampleClient();
  private static final UUID TEST_STORE_ID = UUID.randomUUID();
  private static final Store TEST_STORE = StoreMockEntities.sampleStore(TEST_STORE_ID);
  private static final StoreFollower TEST_FOLLOWER =
      StoreMockEntities.sampleFollower(TEST_CLIENT, TEST_STORE);

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
  void followStore_shouldReturnForbidden_whenNotAuthorized() throws Exception {
    mockMvc
        .perform(post("/api/v1/stores/{storeId}/followers", TEST_STORE_ID))
        .andExpect(status().is(403));
  }

  @Test
  @WithMockUser(username = "testUser")
  void followStore_shouldFollowStore_whenAuthorized() throws Exception {
    when(storeService.followStore(TEST_STORE_ID)).thenReturn(TEST_FOLLOWER);
    mockMvc
        .perform(post("/api/v1/stores/{storeId}/followers", TEST_STORE_ID))
        .andExpect(status().isCreated());
  }

  @Test
  void unfollowStore_shouldReturnForbidden_whenNotAuthorized() throws Exception {
    mockMvc
        .perform(delete("/api/v1/stores/{storeId}/followers/me", TEST_STORE_ID))
        .andExpect(status().is(403));
  }

  @Test
  @WithMockUser(username = "testUser")
  void unfollowStore_shouldUnfollowStore_whenAuthorized() throws Exception {
    doNothing().when(storeService).unfollowStore(TEST_STORE_ID);
    mockMvc
        .perform(delete("/api/v1/stores/{storeId}/followers/me", TEST_STORE_ID))
        .andExpect(status().isOk());
  }

  @Test
  void getMyFollowedStores_shouldReturnForbidden_whenNotAuthorized() throws Exception {
    mockMvc.perform(get("/api/v1/clients/me/followed-stores")).andExpect(status().is(403));
  }

  @Test
  @WithMockUser(username = "testUser")
  void getMyFollowedStores_shouldReturnFollowedStores_whenAuthorized() throws Exception {
    when(storeService.getMyFollowedStores()).thenReturn(List.of(TEST_STORE));
    mockMvc
        .perform(get("/api/v1/clients/me/followed-stores"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value(TEST_STORE_ID.toString()));
  }
}
