package ispp.project.dondesiempre.modules.stores.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import ispp.project.dondesiempre.modules.common.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.stores.dtos.StoreImageDTO;
import ispp.project.dondesiempre.modules.stores.dtos.StoreImageUpdateDTO;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.models.StoreImage;
import ispp.project.dondesiempre.modules.stores.services.StoreImageService;
import ispp.project.dondesiempre.modules.stores.services.StoreService;
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
    controllers = StoreImageController.class,
    excludeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {GlobalExceptionHandler.class}))
@Import(SecurityConfig.class)
public class StoreImageControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private StoreImageService storeImageService;
  @MockitoBean private StoreService storeService;

  @Test
  @WithMockUser
  void shouldReturnOkAndListOfImages_whenGetByStore() throws Exception {
    UUID storeId = UUID.randomUUID();

    Store store = new Store();
    store.setId(storeId);

    StoreImage image1 = new StoreImage();
    image1.setId(UUID.randomUUID());
    image1.setImage("https://example.com/image1.jpg");
    image1.setDisplayOrder(0);
    image1.setStore(store);

    StoreImage image2 = new StoreImage();
    image2.setId(UUID.randomUUID());
    image2.setImage("https://example.com/image2.jpg");
    image2.setDisplayOrder(1);
    image2.setStore(store);

    when(storeService.findById(storeId)).thenReturn(store);
    when(storeImageService.findImageByStoreId(storeId)).thenReturn(List.of(image1, image2));

    mockMvc
        .perform(get("/api/v1/stores/{storeId}/images", storeId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(2))
        .andExpect(jsonPath("$[0].id").value(image1.getId().toString()))
        .andExpect(jsonPath("$[0].image").value("https://example.com/image1.jpg"))
        .andExpect(jsonPath("$[0].displayOrder").value(0))
        .andExpect(jsonPath("$[1].id").value(image2.getId().toString()))
        .andExpect(jsonPath("$[1].image").value("https://example.com/image2.jpg"))
        .andExpect(jsonPath("$[1].displayOrder").value(1));

    verify(storeService, times(1)).findById(storeId);
    verify(storeImageService, times(1)).findImageByStoreId(storeId);
  }

  @Test
  @WithMockUser
  void shouldReturnNotFound_whenGetByStoreAndStoreDoesNotExist() throws Exception {
    UUID storeId = UUID.randomUUID();

    when(storeService.findById(storeId))
        .thenThrow(new ResourceNotFoundException("Store with ID " + storeId + " not found."));

    mockMvc
        .perform(get("/api/v1/stores/{storeId}/images", storeId))
        .andExpect(status().isNotFound());

    verify(storeService, times(1)).findById(storeId);
    verify(storeImageService, times(0)).findImageByStoreId(storeId);
  }

  @Test
  @WithMockUser
  void shouldReturnCreated_whenCreateImageSuccessfully() throws Exception {
    UUID storeId = UUID.randomUUID();

    StoreImageUpdateDTO requestDTO = new StoreImageUpdateDTO();
    requestDTO.setImage("https://example.com/new-image.jpg");
    requestDTO.setDisplayOrder(0);

    StoreImageDTO responseDTO = new StoreImageDTO();
    responseDTO.setId(UUID.randomUUID());
    responseDTO.setImage("https://example.com/new-image.jpg");
    responseDTO.setDisplayOrder(0);

    when(storeImageService.add(eq(storeId), any(StoreImageUpdateDTO.class))).thenReturn(responseDTO);

    mockMvc
        .perform(
            post("/api/v1/stores/{storeId}/images", storeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(responseDTO.getId().toString()))
        .andExpect(jsonPath("$.image").value("https://example.com/new-image.jpg"))
        .andExpect(jsonPath("$.displayOrder").value(0));

    verify(storeImageService, times(1)).add(eq(storeId), any(StoreImageUpdateDTO.class));
  }

  @Test
  @WithMockUser
  void shouldReturnBadRequest_whenCreateImageWithInvalidUrl() throws Exception {
    UUID storeId = UUID.randomUUID();

    StoreImageUpdateDTO requestDTO = new StoreImageUpdateDTO();
    requestDTO.setImage("not-a-valid-url");
    requestDTO.setDisplayOrder(0);

    mockMvc
        .perform(
            post("/api/v1/stores/{storeId}/images", storeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser
  void shouldReturnOk_whenUpdateImageSuccessfully() throws Exception {
    UUID storeId = UUID.randomUUID();
    UUID imageId = UUID.randomUUID();

    StoreImageUpdateDTO requestDTO = new StoreImageUpdateDTO();
    requestDTO.setImage("https://example.com/updated-image.jpg");
    requestDTO.setDisplayOrder(2);

    StoreImageDTO responseDTO = new StoreImageDTO();
    responseDTO.setId(imageId);
    responseDTO.setImage("https://example.com/updated-image.jpg");
    responseDTO.setDisplayOrder(2);

    when(storeImageService.update(eq(imageId), any(StoreImageUpdateDTO.class))).thenReturn(responseDTO);

    mockMvc
        .perform(
            put("/api/v1/stores/{storeId}/images/{id}", storeId, imageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(imageId.toString()))
        .andExpect(jsonPath("$.image").value("https://example.com/updated-image.jpg"))
        .andExpect(jsonPath("$.displayOrder").value(2));

    verify(storeImageService, times(1)).update(eq(imageId), any(StoreImageUpdateDTO.class));
  }

  @Test
  @WithMockUser
  void shouldReturnBadRequest_whenUpdateImageWithInvalidUrl() throws Exception {
    UUID storeId = UUID.randomUUID();
    UUID imageId = UUID.randomUUID();

    StoreImageUpdateDTO requestDTO = new StoreImageUpdateDTO();
    requestDTO.setImage("invalid-url");
    requestDTO.setDisplayOrder(1);

    mockMvc
        .perform(
            put("/api/v1/stores/{storeId}/images/{id}", storeId, imageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser
  void shouldReturnNotFound_whenUpdateImageDoesNotExist() throws Exception {
    UUID storeId = UUID.randomUUID();
    UUID imageId = UUID.randomUUID();

    StoreImageUpdateDTO requestDTO = new StoreImageUpdateDTO();
    requestDTO.setImage("https://example.com/updated-image.jpg");
    requestDTO.setDisplayOrder(1);

    when(storeImageService.update(eq(imageId), any(StoreImageUpdateDTO.class)))
        .thenThrow(new ResourceNotFoundException("Image not found"));

    mockMvc
        .perform(
            put("/api/v1/stores/{storeId}/images/{id}", storeId, imageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser
  void shouldReturnOk_whenDeleteImageSuccessfully() throws Exception {
    UUID storeId = UUID.randomUUID();
    UUID imageId = UUID.randomUUID();

    mockMvc
        .perform(delete("/api/v1/stores/{storeId}/images/{id}", storeId, imageId))
        .andExpect(status().isOk())
        .andExpect(content().string("Image successfully removed."));

    verify(storeImageService, times(1)).delete(imageId);
  }

  @Test
  @WithMockUser
  void shouldReturnNotFound_whenDeleteImageDoesNotExist() throws Exception {
    UUID storeId = UUID.randomUUID();
    UUID imageId = UUID.randomUUID();

    doThrow(new ResourceNotFoundException("Image not found"))
        .when(storeImageService)
        .delete(imageId);

    mockMvc
        .perform(delete("/api/v1/stores/{storeId}/images/{id}", storeId, imageId))
        .andExpect(status().isNotFound());
  }

  @Test
@WithMockUser
void shouldReturnBadRequest_whenCreatingMoreThanFiveImages() throws Exception {
  UUID storeId = UUID.randomUUID();

  StoreImageUpdateDTO requestDTO = new StoreImageUpdateDTO();
  requestDTO.setImage("https://example.com/new-image.jpg");
  requestDTO.setDisplayOrder(4);

  when(storeImageService.add(eq(storeId), any(StoreImageUpdateDTO.class)))
      .thenThrow(new InvalidRequestException("A store cannot have more than 5 images."));

  mockMvc
      .perform(
          post("/api/v1/stores/{storeId}/images", storeId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(requestDTO)))
      .andExpect(status().isBadRequest());
}
}