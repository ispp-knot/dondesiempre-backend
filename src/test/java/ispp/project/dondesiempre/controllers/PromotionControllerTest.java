package ispp.project.dondesiempre.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import ispp.project.dondesiempre.controllers.exceptions.GlobalExceptionHandler;
import ispp.project.dondesiempre.controllers.promotions.PromotionController;
import ispp.project.dondesiempre.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.models.promotions.Promotion;
import ispp.project.dondesiempre.models.promotions.dto.PromotionCreationDTO;
import ispp.project.dondesiempre.models.promotions.dto.PromotionUpdateDTO;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.services.UserService;
import ispp.project.dondesiempre.services.promotions.PromotionService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    controllers = PromotionController.class,
    excludeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {GlobalExceptionHandler.class}))
public class PromotionControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private PromotionService promotionService;
  @MockitoBean private UserService userService;

  private static final UUID TEST_PROMOTION_ID = UUID.randomUUID();
  private static final UUID TEST_STORE_ID = UUID.randomUUID();
  private static final UUID TEST_PRODUCT_ID = UUID.randomUUID();

  private static Store sampleStore() {
    Store store = new Store();
    store.setId(TEST_STORE_ID);
    store.setName("Test Store");
    return store;
  }

  private static Promotion samplePromotion() {
    Promotion promotion = new Promotion();
    promotion.setId(TEST_PROMOTION_ID);
    promotion.setName("Test Promotion");
    promotion.setDiscountPercentage(20);
    promotion.setActive(true);
    promotion.setStore(sampleStore());
    return promotion;
  }

  @Test
  @WithMockUser
  void shouldCreateNewPromotion_shouldReturnForbidden_whenNotAuthorized() throws Exception {
    PromotionCreationDTO createDTO = new PromotionCreationDTO();
    createDTO.setName("Test Promotion");
    createDTO.setDiscountPercentage(20);
    createDTO.setActive(true);
    createDTO.setProductIds(List.of(TEST_PRODUCT_ID));
    createDTO.setStoreId(TEST_STORE_ID);

    doThrow(new UnauthorizedException("Not authorized"))
        .when(promotionService)
        .savePromotion(any());

    mockMvc
        .perform(
            post("/api/v1/promotions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
        .andExpect(status().is(403));
  }

  @Test
  @WithMockUser(username = "testUser")
  void shouldCreateNewPromotion_whenAuthorized() throws Exception {
    PromotionCreationDTO createDTO = new PromotionCreationDTO();
    createDTO.setName("Test Promotion");
    createDTO.setDiscountPercentage(20);
    createDTO.setActive(true);
    createDTO.setProductIds(List.of(TEST_PRODUCT_ID));
    createDTO.setStoreId(TEST_STORE_ID);

    when(promotionService.savePromotion(any())).thenReturn(samplePromotion());
    when(promotionService.getAllProductsByPromotionId(TEST_PROMOTION_ID)).thenReturn(List.of());

    mockMvc
        .perform(
            post("/api/v1/promotions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
        .andExpect(status().isOk());
  }

  @Test
  void shouldGetAllPromotions() throws Exception {
    when(promotionService.getAllPromotions()).thenReturn(List.of(samplePromotion()));
    when(promotionService.getAllProductsByPromotionId(TEST_PROMOTION_ID)).thenReturn(List.of());

    mockMvc
        .perform(get("/api/v1/promotions"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1))
        .andExpect(jsonPath("$[0].name").value("Test Promotion"));
  }

  @Test
  void shouldGetPromotionById() throws Exception {
    when(promotionService.getPromotionById(TEST_PROMOTION_ID)).thenReturn(samplePromotion());
    when(promotionService.getAllProductsByPromotionId(TEST_PROMOTION_ID)).thenReturn(List.of());

    mockMvc
        .perform(get("/api/v1/promotions/{id}", TEST_PROMOTION_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(TEST_PROMOTION_ID.toString()))
        .andExpect(jsonPath("$.name").value("Test Promotion"));
  }

  @Test
  @WithMockUser
  void shouldUpdatePromotion_shouldReturnForbidden_whenNotAuthorized() throws Exception {
    PromotionUpdateDTO updateDTO = new PromotionUpdateDTO();
    updateDTO.setDiscountPercentage(30);

    doThrow(new UnauthorizedException("Not authorized"))
        .when(promotionService)
        .updatePromotion(any(UUID.class), any(PromotionUpdateDTO.class));

    mockMvc
        .perform(
            put("/api/v1/promotions/{id}", TEST_PROMOTION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
        .andExpect(status().is(403));
  }

  @Test
  @WithMockUser(username = "testUser")
  void shouldUpdatePromotionDiscount_whenAuthorized() throws Exception {
    PromotionUpdateDTO updateDTO = new PromotionUpdateDTO();
    updateDTO.setDiscountPercentage(30);

    Promotion updatedPromotion = samplePromotion();
    updatedPromotion.setDiscountPercentage(30);

    when(promotionService.updatePromotion(any(UUID.class), any(PromotionUpdateDTO.class)))
        .thenReturn(updatedPromotion);
    when(promotionService.getAllProductsByPromotionId(TEST_PROMOTION_ID)).thenReturn(List.of());

    mockMvc
        .perform(
            put("/api/v1/promotions/{id}", TEST_PROMOTION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.discountPercentage").value(30));
  }

  @Test
  @WithMockUser(username = "testUser")
  void shouldUpdatePromotionName_whenAuthorized() throws Exception {
    PromotionUpdateDTO updateDTO = new PromotionUpdateDTO();
    updateDTO.setName("Updated Promotion Name");

    Promotion updatedPromotion = samplePromotion();
    updatedPromotion.setName("Updated Promotion Name");

    when(promotionService.updatePromotion(any(UUID.class), any(PromotionUpdateDTO.class)))
        .thenReturn(updatedPromotion);
    when(promotionService.getAllProductsByPromotionId(TEST_PROMOTION_ID)).thenReturn(List.of());

    mockMvc
        .perform(
            put("/api/v1/promotions/{id}", TEST_PROMOTION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated Promotion Name"));
  }

  @Test
  @WithMockUser(username = "testUser")
  void shouldUpdatePromotionActiveStatus_whenAuthorized() throws Exception {
    PromotionUpdateDTO updateDTO = new PromotionUpdateDTO();
    updateDTO.setActive(false);

    Promotion updatedPromotion = samplePromotion();
    updatedPromotion.setActive(false);

    when(promotionService.updatePromotion(any(UUID.class), any(PromotionUpdateDTO.class)))
        .thenReturn(updatedPromotion);
    when(promotionService.getAllProductsByPromotionId(TEST_PROMOTION_ID)).thenReturn(List.of());

    mockMvc
        .perform(
            put("/api/v1/promotions/{id}", TEST_PROMOTION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.active").value(false));
  }

  @Test
  @WithMockUser(username = "testUser")
  void shouldUpdateMultiplePromotionFields_whenAuthorized() throws Exception {
    PromotionUpdateDTO updateDTO = new PromotionUpdateDTO();
    updateDTO.setName("Super Sale");
    updateDTO.setDiscountPercentage(50);
    updateDTO.setActive(false);

    Promotion updatedPromotion = samplePromotion();
    updatedPromotion.setName("Super Sale");
    updatedPromotion.setDiscountPercentage(50);
    updatedPromotion.setActive(false);

    when(promotionService.updatePromotion(any(UUID.class), any(PromotionUpdateDTO.class)))
        .thenReturn(updatedPromotion);
    when(promotionService.getAllProductsByPromotionId(TEST_PROMOTION_ID)).thenReturn(List.of());

    mockMvc
        .perform(
            put("/api/v1/promotions/{id}", TEST_PROMOTION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Super Sale"))
        .andExpect(jsonPath("$.discountPercentage").value(50))
        .andExpect(jsonPath("$.active").value(false));
  }

  @Test
  @WithMockUser
  void shouldDeletePromotion_shouldReturnForbidden_whenNotAuthorized() throws Exception {
    doThrow(new UnauthorizedException("Not authorized"))
        .when(promotionService)
        .deletePromotion(any(UUID.class));

    mockMvc
        .perform(delete("/api/v1/promotions/{id}", TEST_PROMOTION_ID))
        .andExpect(status().is(403));
  }

  @Test
  @WithMockUser(username = "testUser")
  void shouldDeletePromotion_whenAuthorized() throws Exception {
    mockMvc
        .perform(delete("/api/v1/promotions/{id}", TEST_PROMOTION_ID))
        .andExpect(status().isNoContent());
  }
}
