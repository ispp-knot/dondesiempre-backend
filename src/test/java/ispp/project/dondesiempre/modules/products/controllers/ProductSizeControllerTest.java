package ispp.project.dondesiempre.modules.products.controllers;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ispp.project.dondesiempre.modules.products.models.ProductSize;
import ispp.project.dondesiempre.modules.products.services.ProductSizeService;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ProductSizeController.class)
public class ProductSizeControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ProductSizeService productSizeService;

  @Test
  @WithMockUser
  void getAllProductSizes_shouldReturnOk() throws Exception {
    given(productSizeService.getAllProductSizes()).willReturn(Collections.emptyList());

    mockMvc.perform(get("/api/v1/product-sizes")).andExpect(status().isOk());
  }

  @Test
  @WithMockUser
  void getAllProductSizes_shouldReturnListOfSizes() throws Exception {
    ProductSize size1 = new ProductSize();
    size1.setId(UUID.randomUUID());
    size1.setSize("M");

    ProductSize size2 = new ProductSize();
    size2.setId(UUID.randomUUID());
    size2.setSize("L");

    List<ProductSize> sizes = List.of(size1, size2);
    given(productSizeService.getAllProductSizes()).willReturn(sizes);

    mockMvc
        .perform(get("/api/v1/product-sizes"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  @WithMockUser
  void getProductSizeById_shouldReturnOk() throws Exception {
    ProductSize size = new ProductSize();
    size.setId(UUID.randomUUID());
    size.setSize("XL");

    given(productSizeService.getProductSizeById(size.getId())).willReturn(size);

    mockMvc
        .perform(get("/api/v1/product-sizes/{id}", size.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("XL"));
  }

  @Test
  @WithMockUser
  void getProductSizeById_shouldReturnNotFoundWhenSizeDoesNotExist() throws Exception {
    UUID randomId = UUID.randomUUID();
    given(productSizeService.getProductSizeById(randomId))
        .willThrow(new RuntimeException("ProductSize not found with id: " + randomId));

    mockMvc
        .perform(get("/api/v1/product-sizes/{id}", randomId))
        .andExpect(status().is5xxServerError());
  }

  @Test
  @WithMockUser
  void getProductSizeById_shouldReturnCorrectSizeData() throws Exception {
    UUID sizeId = UUID.randomUUID();
    ProductSize size = new ProductSize();
    size.setId(sizeId);
    size.setSize("S");

    given(productSizeService.getProductSizeById(sizeId)).willReturn(size);

    mockMvc
        .perform(get("/api/v1/product-sizes/{id}", sizeId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(sizeId.toString()))
        .andExpect(jsonPath("$.name").value("S"));
  }

  @Test
  @WithMockUser
  void getAllProductSizes_shouldReturnEmptyListWhenNoSizes() throws Exception {
    given(productSizeService.getAllProductSizes()).willReturn(Collections.emptyList());

    mockMvc
        .perform(get("/api/v1/product-sizes"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
  }
}
