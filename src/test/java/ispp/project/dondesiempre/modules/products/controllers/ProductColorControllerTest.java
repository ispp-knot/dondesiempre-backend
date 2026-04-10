package ispp.project.dondesiempre.modules.products.controllers;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ispp.project.dondesiempre.modules.products.models.ProductColor;
import ispp.project.dondesiempre.modules.products.services.ProductColorService;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ProductColorController.class)
public class ProductColorControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ProductColorService productColorService;

  @Test
  @WithMockUser
  void getAllProductColors_shouldReturnOk() throws Exception {
    given(productColorService.getAllProductColors()).willReturn(Collections.emptyList());

    mockMvc.perform(get("/api/v1/product-colors")).andExpect(status().isOk());
  }

  @Test
  @WithMockUser
  void getAllProductColors_shouldReturnListOfColors() throws Exception {
    ProductColor color1 = new ProductColor();
    color1.setId(UUID.randomUUID());
    color1.setColor("Red");

    ProductColor color2 = new ProductColor();
    color2.setId(UUID.randomUUID());
    color2.setColor("Blue");

    List<ProductColor> colors = List.of(color1, color2);
    given(productColorService.getAllProductColors()).willReturn(colors);

    mockMvc
        .perform(get("/api/v1/product-colors"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  @WithMockUser
  void getProductColorById_shouldReturnOk() throws Exception {
    ProductColor color = new ProductColor();
    color.setId(UUID.randomUUID());
    color.setColor("Green");

    given(productColorService.getProductColorById(color.getId())).willReturn(color);

    mockMvc
        .perform(get("/api/v1/product-colors/{id}", color.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Green"));
  }

  @Test
  @WithMockUser
  void getProductColorById_shouldReturnNotFoundWhenColorDoesNotExist() throws Exception {
    UUID randomId = UUID.randomUUID();
    given(productColorService.getProductColorById(randomId))
        .willThrow(new RuntimeException("ProductColor not found with id: " + randomId));

    mockMvc
        .perform(get("/api/v1/product-colors/{id}", randomId))
        .andExpect(status().is5xxServerError());
  }

  @Test
  @WithMockUser
  void getProductColorById_shouldReturnCorrectColorData() throws Exception {
    UUID colorId = UUID.randomUUID();
    ProductColor color = new ProductColor();
    color.setId(colorId);
    color.setColor("Yellow");

    given(productColorService.getProductColorById(colorId)).willReturn(color);

    mockMvc
        .perform(get("/api/v1/product-colors/{id}", colorId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(colorId.toString()))
        .andExpect(jsonPath("$.name").value("Yellow"));
  }

  @Test
  @WithMockUser
  void getAllProductColors_shouldReturnEmptyListWhenNoColors() throws Exception {
    given(productColorService.getAllProductColors()).willReturn(Collections.emptyList());

    mockMvc
        .perform(get("/api/v1/product-colors"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
  }
}
