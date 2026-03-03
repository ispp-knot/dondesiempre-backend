package ispp.project.dondesiempre.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import ispp.project.dondesiempre.controllers.exceptions.GlobalExceptionHandler;
import ispp.project.dondesiempre.controllers.storefronts.StorefrontController;
import ispp.project.dondesiempre.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.models.storefronts.Storefront;
import ispp.project.dondesiempre.models.storefronts.dto.StorefrontDTO;
import ispp.project.dondesiempre.services.UserService;
import ispp.project.dondesiempre.services.storefronts.StorefrontService;
import java.util.UUID;
import org.junit.jupiter.api.Disabled;
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

  @MockitoBean private UserService userService;

  @Test
  void getStorefront_shouldReturnStorefrontDTO_whenValidIdProvided() throws Exception {
    UUID id = UUID.randomUUID();
    Storefront storefront = new Storefront();
    storefront.setId(id);
    storefront.setPrimaryColor("#c65a3a");

    // Ahora usamos findById porque getDTOById ya no existe
    when(storefrontService.findById(id)).thenReturn(storefront);

    mockMvc
        .perform(get("/api/v1/storefronts/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.primaryColor").value("#c65a3a"));
  }

  @Test
  void updateStorefront_shouldUpdateStorefront_whenUserIsOwner() throws Exception {
    UUID id = UUID.randomUUID();
    Storefront storefront = new Storefront();
    StorefrontDTO inputDto = new StorefrontDTO();
    inputDto.setPrimaryColor("#000000");

    StorefrontDTO updatedDto = new StorefrontDTO();
    updatedDto.setPrimaryColor("#000000");

    when(storefrontService.findById(id)).thenReturn(storefront);
    when(storefrontService.updateStorefront(eq(id), any(StorefrontDTO.class)))
        .thenReturn(updatedDto);

    mockMvc
        .perform(
            put("/api/v1/storefronts/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.primaryColor").value("#000000"));
  }

  @Disabled("This test is disabled")
  @Test
  void updateStorefront_shouldReturnForbidden_whenUserIsNotOwner() throws Exception {
    UUID id = UUID.randomUUID();
    Storefront storefront = new Storefront();
    StorefrontDTO inputDto = new StorefrontDTO();

    when(storefrontService.findById(id)).thenReturn(storefront);

    doThrow(new UnauthorizedException("You do not own this store."))
        .when(userService)
        .assertUserOwnsStore(any());

    mockMvc
        .perform(
            put("/api/v1/storefronts/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
        .andExpect(status().isForbidden());
  }

  @Test
  void getStorefront_shouldReturnBadRequest_whenInvalidUUIDProvided() throws Exception {
    mockMvc.perform(get("/api/v1/storefronts/invalid-uuid")).andExpect(status().isBadRequest());
  }
}
