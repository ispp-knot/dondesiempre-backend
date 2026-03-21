package ispp.project.dondesiempre.modules.products.controllers;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ispp.project.dondesiempre.config.GlobalExceptionHandler;
import ispp.project.dondesiempre.modules.products.models.ProductType;
import ispp.project.dondesiempre.modules.products.services.ProductTypeService;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    controllers = ProductTypeController.class,
    excludeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {GlobalExceptionHandler.class}))
public class ProductTypeControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ProductTypeService productTypeService;

  @Test
  void getAllProductTypes_shouldReturnOk() throws Exception {
    given(productTypeService.findAll()).willReturn(Collections.emptyList());

    mockMvc.perform(get("/api/v1/product-types")).andExpect(status().isOk());
  }

  @Test
  void getProductTypeById_shouldReturnOk() throws Exception {
    ProductType productType = new ProductType();
    productType.setType("Test");
    UUID id = UUID.randomUUID();

    given(productTypeService.getProductTypeById(id)).willReturn(productType);

    mockMvc
        .perform(get("/api/v1/product-types/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Test"));
  }
}
