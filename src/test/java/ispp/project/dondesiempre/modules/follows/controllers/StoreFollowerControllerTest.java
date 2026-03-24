package ispp.project.dondesiempre.modules.follows.controllers;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ispp.project.dondesiempre.config.GlobalExceptionHandler;
import ispp.project.dondesiempre.mockEntities.StoreMockEntities;
import ispp.project.dondesiempre.modules.auth.services.UserService;
import ispp.project.dondesiempre.modules.clients.models.Client;
import ispp.project.dondesiempre.modules.follows.models.StoreFollower;
import ispp.project.dondesiempre.modules.follows.services.StoreFollowerService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    controllers = StoreFollowerController.class,
    excludeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {GlobalExceptionHandler.class}))
public class StoreFollowerControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private StoreFollowerService storeFollowerService;
  @MockitoBean private StoreService storeService;
  @MockitoBean private UserService userService;

  private static final Client TEST_CLIENT = StoreMockEntities.sampleClient();
  private static final UUID TEST_STORE_ID = UUID.randomUUID();
  private static final Store TEST_STORE = StoreMockEntities.sampleStore(TEST_STORE_ID);
  private static final StoreFollower TEST_FOLLOWER =
      StoreMockEntities.sampleFollower(TEST_CLIENT, TEST_STORE);

  @Test
  @WithMockUser(username = "testUser")
  void followStore_shouldFollowStore_whenAuthorized() throws Exception {
    when(storeFollowerService.followStore(TEST_STORE_ID)).thenReturn(TEST_FOLLOWER);
    mockMvc
        .perform(post("/api/v1/stores/{storeId}/follow", TEST_STORE_ID))
        .andExpect(status().isCreated());
  }

  /*
   * @Test
   * void unfollowStore_shouldReturnForbidden_whenNotAuthorized() throws Exception
   * {
   * mockMvc
   * .perform(delete("/api/v1/stores/{storeId}/followers/me", TEST_STORE_ID))
   * .andExpect(status().is(403));
   * }
   */

  @Test
  @WithMockUser(username = "testUser")
  void unfollowStore_shouldUnfollowStore_whenAuthorized() throws Exception {
    doNothing().when(storeFollowerService).unfollowStore(TEST_STORE_ID);
    mockMvc
        .perform(delete("/api/v1/stores/{storeId}/follow", TEST_STORE_ID))
        .andExpect(status().isOk());
  }

  /*
   * @Test
   * void getMyFollowedStores_shouldReturnForbidden_whenNotAuthorized() throws
   * Exception {
   * mockMvc.perform(get("/api/v1/clients/me/followed-stores")).andExpect(status()
   * .is(403));
   * }
   */

  @Test
  @WithMockUser(username = "testUser")
  void getMyFollowedStores_shouldReturnFollowedStores_whenAuthorized() throws Exception {
    StoreDTO storeDTO = new StoreDTO();
    storeDTO.setId(TEST_STORE_ID);

    when(userService.getCurrentClient()).thenReturn(TEST_CLIENT);
    when(storeFollowerService.getMyFollowedStores()).thenReturn(List.of(TEST_STORE));
    when(storeService.toDTO(TEST_STORE)).thenReturn(storeDTO);

    mockMvc
        .perform(get("/api/v1/clients/{clientId}/following", TEST_CLIENT.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value(TEST_STORE_ID.toString()));
  }

  /*
   * @Test
   * void checkIfIFollowStore_shouldReturnForbidden_whenNotAuthorized() throws
   * Exception {
   * mockMvc
   * .perform(get("/api/v1/stores/{storeId}/followers/me", TEST_STORE_ID))
   * .andExpect(status().is(403));
   * }
   */
  @Test
  @WithMockUser(username = "testUser")
  void checkIfIFollowStore_shouldReturnFollowingDTO_whenAuthorizedAndFollowing() throws Exception {
    when(userService.getCurrentClient()).thenReturn(TEST_CLIENT);
    when(storeFollowerService.checkIfClientFollowsStore(TEST_CLIENT.getId(), TEST_STORE_ID))
        .thenReturn(true);
    mockMvc
        .perform(get("/api/v1/stores/{storeId}/follow", TEST_STORE_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.clientId").value(TEST_CLIENT.getId().toString()))
        .andExpect(jsonPath("$.storeId").value(TEST_STORE_ID.toString()))
        .andExpect(jsonPath("$.isFollowing").value(true));
  }

  @Test
  @WithMockUser(username = "testUser")
  void checkIfIFollowStore_shouldReturnNotFollowingDTO_whenAuthorizedAndNotFollowing()
      throws Exception {
    when(userService.getCurrentClient()).thenReturn(TEST_CLIENT);
    when(storeFollowerService.checkIfClientFollowsStore(TEST_CLIENT.getId(), TEST_STORE_ID))
        .thenReturn(false);
    mockMvc
        .perform(get("/api/v1/stores/{storeId}/follow", TEST_STORE_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.clientId").value(TEST_CLIENT.getId().toString()))
        .andExpect(jsonPath("$.storeId").value(TEST_STORE_ID.toString()))
        .andExpect(jsonPath("$.isFollowing").value(false));
  }
}
