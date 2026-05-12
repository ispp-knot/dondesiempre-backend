package ispp.project.dondesiempre.modules.products.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import ispp.project.dondesiempre.modules.products.dtos.ProductSizeCreationDTO;
import ispp.project.dondesiempre.modules.products.models.ProductSize;
import ispp.project.dondesiempre.modules.products.services.ProductSizeService;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ProductSizeController.class)
public class ProductSizeControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private ProductSizeService productSizeService;

  @Test
  @WithMockUser
  void getAllProductSizes_shouldReturnOk() throws Exception {
    when(productSizeService.getAllProductSizes()).thenReturn(Collections.emptyList());

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
    when(productSizeService.getAllProductSizes()).thenReturn(sizes);

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

    when(productSizeService.getProductSizeById(size.getId())).thenReturn(size);

    mockMvc
        .perform(get("/api/v1/product-sizes/{id}", size.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("XL"));
  }

  @Test
  @WithMockUser
  void getProductSizeById_shouldReturnNotFoundWhenSizeDoesNotExist() throws Exception {
    UUID randomId = UUID.randomUUID();
    when(productSizeService.getProductSizeById(randomId))
        .thenThrow(new RuntimeException("ProductSize not found with id: " + randomId));

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

    when(productSizeService.getProductSizeById(sizeId)).thenReturn(size);

    mockMvc
        .perform(get("/api/v1/product-sizes/{id}", sizeId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(sizeId.toString()))
        .andExpect(jsonPath("$.name").value("S"));
  }

  @Test
  @WithMockUser
  void getAllProductSizes_shouldReturnEmptyListWhenNoSizes() throws Exception {
    when(productSizeService.getAllProductSizes()).thenReturn(Collections.emptyList());

    mockMvc
        .perform(get("/api/v1/product-sizes"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  @WithMockUser
  void createProductSize_shouldReturnCreated() throws Exception {
    ProductSize size = new ProductSize();
    size.setId(UUID.randomUUID());
    size.setSize("XS");
    ProductSizeCreationDTO dto = new ProductSizeCreationDTO();
    dto.setSize("XS");

    when(productSizeService.createProductSize(any())).thenReturn(size);

    mockMvc
        .perform(
            post("/api/v1/product-sizes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("XS"));
  }

  @Test
  @WithMockUser
  void createProductSize_shouldReturnBadRequestWhenBodyIsEmpty() throws Exception {
    ProductSizeCreationDTO dto = new ProductSizeCreationDTO();
    mockMvc
        .perform(
            post("/api/v1/product-sizes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isBadRequest());
  }
}
