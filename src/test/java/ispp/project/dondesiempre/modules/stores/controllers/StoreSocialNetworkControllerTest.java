package ispp.project.dondesiempre.modules.stores.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import ispp.project.dondesiempre.config.GlobalExceptionHandler;
import ispp.project.dondesiempre.config.security.SecurityConfig;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.stores.dtos.SocialNetworkDTO;
import ispp.project.dondesiempre.modules.stores.dtos.SocialNetworkUpdateDTO;
import ispp.project.dondesiempre.modules.stores.models.SocialNetwork;
import ispp.project.dondesiempre.modules.stores.models.StoreSocialNetwork;
import ispp.project.dondesiempre.modules.stores.services.StoreService;
import ispp.project.dondesiempre.modules.stores.services.StoreSocialNetworkService;
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
    controllers = StoreSocialNetworkController.class,
    excludeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {GlobalExceptionHandler.class}))
@Import(SecurityConfig.class)
public class StoreSocialNetworkControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private StoreService storeService;
  @MockitoBean private StoreSocialNetworkService storeSocialNetworkService;
  private static final java.util.UUID TEST_STORE_ID = java.util.UUID.randomUUID();
  private static final ispp.project.dondesiempre.modules.stores.models.Store TEST_STORE =
      ispp.project.dondesiempre.mockEntities.StoreMockEntities.sampleStore(TEST_STORE_ID);

  @Test
  @WithMockUser
  void getByStoreId_shouldReturnOk_whenStoreExists() throws Exception {
    UUID storeId = UUID.randomUUID();

    when(storeService.findById(storeId)).thenReturn(null);

    StoreSocialNetwork ssn = new StoreSocialNetwork();
    ssn.setId(UUID.randomUUID());
    ssn.setLink("http://test.com");

    SocialNetwork socialNetwork = new SocialNetwork();
    socialNetwork.setName("Instagram");
    ssn.setSocialNetwork(socialNetwork);

    when(storeSocialNetworkService.findByStoreId(storeId)).thenReturn(List.of(ssn));

    mockMvc
        .perform(get("/api/v1/stores/" + storeId + "/social-networks"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1))
        .andExpect(jsonPath("$[0].name").value("Instagram"))
        .andExpect(jsonPath("$[0].link").value("http://test.com"));
  }

  @Test
  @WithMockUser
  void create_shouldReturnCreated_whenValidDTO() throws Exception {
    UUID storeId = UUID.randomUUID();

    SocialNetworkDTO dto = new SocialNetworkDTO();
    dto.setName("Instagram");
    dto.setLink("http://instagram.com/test");

    StoreSocialNetwork ssn = new StoreSocialNetwork();
    ssn.setId(UUID.randomUUID());
    ssn.setLink(dto.getLink());

    SocialNetwork socialNetwork = new SocialNetwork();
    socialNetwork.setName(dto.getName());
    ssn.setSocialNetwork(socialNetwork);

    when(storeSocialNetworkService.addStoreSocialNetwork(eq(storeId), any())).thenReturn(ssn);

    mockMvc
        .perform(
            post("/api/v1/stores/" + storeId + "/social-networks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(ssn.getId().toString()))
        .andExpect(jsonPath("$.name").value("Instagram"))
        .andExpect(jsonPath("$.link").value("http://instagram.com/test"));
  }

  @Test
  @WithMockUser
  void create_shouldReturnNotFound_whenStoreDoesNotExist() throws Exception {
    UUID storeId = UUID.randomUUID();

    SocialNetworkDTO dto = new SocialNetworkDTO();
    dto.setName("Instagram");
    dto.setLink("http://test.com");

    when(storeSocialNetworkService.addStoreSocialNetwork(eq(storeId), any()))
        .thenThrow(new ResourceNotFoundException("Store not found"));

    mockMvc
        .perform(
            post("/api/v1/stores/" + storeId + "/social-networks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser
  void create_shouldReturnForbidden_whenUserIsNotOwner() throws Exception {
    UUID storeId = UUID.randomUUID();

    SocialNetworkDTO dto = new SocialNetworkDTO();
    dto.setName("Instagram");
    dto.setLink("http://instagram.com/test");

    when(storeSocialNetworkService.addStoreSocialNetwork(eq(storeId), any()))
        .thenThrow(new org.springframework.security.access.AccessDeniedException("Not owner"));

    mockMvc
        .perform(
            post("/api/v1/stores/" + storeId + "/social-networks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser
  void update_shouldReturnOk_whenStoreSocialNetworkExists() throws Exception {
    UUID id = UUID.randomUUID();

    SocialNetworkUpdateDTO dto = new SocialNetworkUpdateDTO();
    dto.setLink("http://updated.com");

    StoreSocialNetwork ssn = new StoreSocialNetwork();
    ssn.setId(id);
    ssn.setLink(dto.getLink());

    SocialNetwork socialNetwork = new SocialNetwork();
    socialNetwork.setName("Instagram");
    ssn.setSocialNetwork(socialNetwork);

    when(storeSocialNetworkService.update(eq(id), any())).thenReturn(ssn);

    mockMvc
        .perform(
            put("/api/v1/store-social-networks/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.name").value("Instagram"))
        .andExpect(jsonPath("$.link").value("http://updated.com"));
  }

  @Test
  @WithMockUser
  void update_shouldReturnForbidden_whenUserIsNotOwner() throws Exception {
    UUID id = UUID.randomUUID();

    SocialNetworkUpdateDTO dto = new SocialNetworkUpdateDTO();
    dto.setLink("http://updated.com");

    when(storeSocialNetworkService.update(eq(id), any()))
        .thenThrow(new org.springframework.security.access.AccessDeniedException("Not owner"));

    mockMvc
        .perform(
            put("/api/v1/store-social-networks/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser
  void delete_shouldReturnOk_whenStoreSocialNetworkExists() throws Exception {
    UUID id = UUID.randomUUID();

    doNothing().when(storeSocialNetworkService).delete(id);

    mockMvc
        .perform(delete("/api/v1/store-social-networks/" + id))
        .andExpect(status().isOk())
        .andExpect(content().string("Social network successfully removed."));
  }

  @Test
  @WithMockUser
  void getByStoreId_shouldReturnNotFound_whenStoreDoesNotExist() throws Exception {
    UUID storeId = UUID.randomUUID();

    when(storeService.findById(storeId))
        .thenThrow(new ResourceNotFoundException("Store not found"));

    mockMvc
        .perform(get("/api/v1/stores/" + storeId + "/social-networks"))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser
  void delete_shouldReturnForbidden_whenUserIsNotOwner() throws Exception {
    UUID id = UUID.randomUUID();

    doThrow(new org.springframework.security.access.AccessDeniedException("Not owner"))
        .when(storeSocialNetworkService)
        .delete(id);

    mockMvc
        .perform(delete("/api/v1/store-social-networks/" + id))
        .andExpect(status().isForbidden());
  }
}
