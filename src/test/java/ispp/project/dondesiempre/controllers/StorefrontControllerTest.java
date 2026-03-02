package ispp.project.dondesiempre.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import ispp.project.dondesiempre.controllers.exceptions.GlobalExceptionHandler;
import ispp.project.dondesiempre.controllers.storefronts.StorefrontController;
import ispp.project.dondesiempre.models.storefronts.dto.StorefrontDTO;
import ispp.project.dondesiempre.services.storefronts.StorefrontService;
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
    controllers = StorefrontController.class,
    excludeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {GlobalExceptionHandler.class}))
public class StorefrontControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private StorefrontService storefrontService;

  @Test
  void shouldReturnStorefrontDTO_whenValidIdProvided() throws Exception {
    UUID id = UUID.randomUUID();
    StorefrontDTO dto = new StorefrontDTO();
    dto.setPrimaryColor("#c65a3a");
    dto.setSecondaryColor("#19756a");
    dto.setIsFirstCollections(true);

    when(storefrontService.getDTOById(id)).thenReturn(dto);

    mockMvc
        .perform(get("/api/v1/storefronts/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.primaryColor").value("#c65a3a"))
        .andExpect(jsonPath("$.secondaryColor").value("#19756a"))
        .andExpect(jsonPath("$.isFirstCollections").value(true));
  }

  @Test
  void shouldUpdateStorefront_whenValidDTOProvided() throws Exception {
    UUID id = UUID.randomUUID();
    StorefrontDTO inputDto = new StorefrontDTO();
    inputDto.setPrimaryColor("#000000");
    inputDto.setBannerImageUrl("http://example.com/banner.png");

    StorefrontDTO updatedDto = new StorefrontDTO();
    updatedDto.setPrimaryColor("#000000");
    updatedDto.setBannerImageUrl("http://example.com/banner.png");
    updatedDto.setSecondaryColor("#19756a");

    when(storefrontService.updateStorefront(eq(id), any(StorefrontDTO.class)))
        .thenReturn(updatedDto);

    mockMvc
        .perform(
            put("/api/v1/storefronts/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.primaryColor").value("#000000"))
        .andExpect(jsonPath("$.bannerImageUrl").value("http://example.com/banner.png"))
        .andExpect(jsonPath("$.secondaryColor").value("#19756a"));
  }

  @Test
  void shouldReturnBadRequest_whenInvalidUUIDProvided() throws Exception {
    mockMvc.perform(get("/api/v1/storefronts/invalid-uuid")).andExpect(status().isBadRequest());
  }
}
