package ispp.project.dondesiempre.modules.stores.controllers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import ispp.project.dondesiempre.config.GlobalExceptionHandler;
import ispp.project.dondesiempre.config.security.SecurityConfig;
import ispp.project.dondesiempre.modules.stores.services.SocialNetworkService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    controllers = SocialNetworkController.class,
    excludeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {GlobalExceptionHandler.class}))
@Import(SecurityConfig.class)
public class SocialNetworkControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private SocialNetworkService socialNetworkService;

  private static final java.util.UUID TEST_STORE_ID = java.util.UUID.randomUUID();
  private static final ispp.project.dondesiempre.modules.stores.models.Store TEST_STORE =
      ispp.project.dondesiempre.mockEntities.StoreMockEntities.sampleStore(TEST_STORE_ID);

  @Test
  @WithMockUser
  void shouldReturnListOfNames_whenSearchingSocialNetworks() throws Exception {
    List<String> mockNames = List.of("Instagram", "Facebook", "TikTok");

    when(socialNetworkService.findAllNames()).thenReturn(mockNames);

    mockMvc
        .perform(get("/api/v1/social-networks/names"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0]").value("Instagram"))
        .andExpect(jsonPath("$[1]").value("Facebook"))
        .andExpect(jsonPath("$[2]").value("TikTok"));

    verify(socialNetworkService).findAllNames();
  }
}
