package ispp.project.dondesiempre.modules.products.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import ispp.project.dondesiempre.config.GlobalExceptionHandler;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.products.dtos.ProductVariantCreationDTO;
import ispp.project.dondesiempre.modules.products.dtos.ProductVariantUpdateDTO;
import ispp.project.dondesiempre.modules.products.models.Product;
import ispp.project.dondesiempre.modules.products.models.ProductColor;
import ispp.project.dondesiempre.modules.products.models.ProductSize;
import ispp.project.dondesiempre.modules.products.models.ProductVariant;
import ispp.project.dondesiempre.modules.products.services.ProductVariantService;
import ispp.project.dondesiempre.modules.stores.models.Store;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
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
    controllers = ProductVariantController.class,
    excludeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {GlobalExceptionHandler.class}))
public class ProductVariantControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private ProductVariantService productVariantService;

  private UUID variantId;
  private UUID productId;
  private UUID sizeId;
  private UUID colorId;
  private UUID storeId;

  private Store store;
  private Product product;
  private ProductSize size;
  private ProductColor color;
  private ProductVariant variant;

  @BeforeEach
  void setUp() {
    variantId = UUID.randomUUID();
    productId = UUID.randomUUID();
    sizeId = UUID.randomUUID();
    colorId = UUID.randomUUID();
    storeId = UUID.randomUUID();

    store = new Store();
    store.setId(storeId);

    product = new Product();
    product.setId(productId);
    product.setName("Test Product");
    product.setPriceInCents(1000);
    product.setStore(store);

    size = new ProductSize();
    size.setId(sizeId);
    size.setSize("M");

    color = new ProductColor();
    color.setId(colorId);
    color.setColor("Red");

    variant = new ProductVariant();
    variant.setId(variantId);
    variant.setProduct(product);
    variant.setSize(size);
    variant.setColor(color);
    variant.setIsAvailable(true);
  }

  @Test
  @WithMockUser
  public void shouldCreateNewProductVariant() throws Exception {
    ProductVariantCreationDTO dto = new ProductVariantCreationDTO();
    dto.setProductId(productId);
    dto.setSizeId(sizeId);
    dto.setColorId(colorId);
    dto.setIsAvailable(true);

    when(productVariantService.createProductVariant(any())).thenReturn(variant);

    mockMvc
        .perform(
            post("/api/v1/product-variants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(variantId.toString()))
        .andExpect(jsonPath("$.productId").value(productId.toString()))
        .andExpect(jsonPath("$.sizeId").value(sizeId.toString()))
        .andExpect(jsonPath("$.colorId").value(colorId.toString()))
        .andExpect(jsonPath("$.isAvailable").value(true));
  }

  @Test
  @WithMockUser
  public void shouldThrowException_WhenCreatingVariantWithNonExistentProduct() throws Exception {
    ProductVariantCreationDTO dto = new ProductVariantCreationDTO();
    dto.setProductId(UUID.randomUUID());
    dto.setSizeId(sizeId);
    dto.setColorId(colorId);
    dto.setIsAvailable(true);

    when(productVariantService.createProductVariant(any()))
        .thenThrow(new ResourceNotFoundException("Product not found"));

    mockMvc
        .perform(
            post("/api/v1/product-variants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldThrowUnauthorizedException_WhenCreatingVariant() throws Exception {
    ProductVariantCreationDTO dto = new ProductVariantCreationDTO();
    dto.setProductId(productId);
    dto.setSizeId(sizeId);
    dto.setColorId(colorId);
    dto.setIsAvailable(true);

    mockMvc
        .perform(
            post("/api/v1/product-variants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser
  public void shouldGetProductVariantById() throws Exception {
    when(productVariantService.getProductVariantById(variantId)).thenReturn(variant);

    mockMvc
        .perform(get("/api/v1/product-variants/" + variantId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(variantId.toString()))
        .andExpect(jsonPath("$.productId").value(productId.toString()))
        .andExpect(jsonPath("$.isAvailable").value(true));
  }

  @Test
  @WithMockUser
  public void shouldThrowException_WhenGettingNonExistentVariant() throws Exception {
    UUID nonExistentId = UUID.randomUUID();
    when(productVariantService.getProductVariantById(nonExistentId))
        .thenThrow(
            new ResourceNotFoundException("ProductVariant not found with id: " + nonExistentId));

    mockMvc
        .perform(get("/api/v1/product-variants/" + nonExistentId))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser
  public void shouldGetAllProductVariants() throws Exception {
    when(productVariantService.getAllProductVariants()).thenReturn(List.of(variant));

    mockMvc
        .perform(get("/api/v1/product-variants"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1))
        .andExpect(jsonPath("$[0].id").value(variantId.toString()))
        .andExpect(jsonPath("$[0].isAvailable").value(true));
  }

  @Test
  @WithMockUser
  public void shouldGetVariantsByProductId() throws Exception {
    when(productVariantService.getVariantsByProductId(productId)).thenReturn(List.of(variant));

    mockMvc
        .perform(get("/api/v1/product-variants/product/" + productId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1))
        .andExpect(jsonPath("$[0].productId").value(productId.toString()));
  }

  @Test
  @WithMockUser
  public void shouldGetAvailableVariantsByProductId() throws Exception {
    when(productVariantService.getAvailableVariantsByProductId(productId))
        .thenReturn(List.of(variant));

    mockMvc
        .perform(get("/api/v1/product-variants/product/" + productId + "/available"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1))
        .andExpect(jsonPath("$[0].isAvailable").value(true));
  }

  @Test
  @WithMockUser
  public void shouldUpdateProductVariant() throws Exception {
    ProductVariantUpdateDTO updateDto = new ProductVariantUpdateDTO();
    updateDto.setIsAvailable(false);

    ProductVariant updatedVariant = new ProductVariant();
    updatedVariant.setId(variantId);
    updatedVariant.setProduct(product);
    updatedVariant.setSize(size);
    updatedVariant.setColor(color);
    updatedVariant.setIsAvailable(false);

    when(productVariantService.updateProductVariant(any(), any())).thenReturn(updatedVariant);

    mockMvc
        .perform(
            put("/api/v1/product-variants/" + variantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$.id").value(variantId.toString()))
        .andExpect(jsonPath("$.isAvailable").value(false));
  }

  @Test
  @WithMockUser
  public void shouldThrowException_WhenUpdatingNonExistentVariant() throws Exception {
    UUID nonExistentId = UUID.randomUUID();
    ProductVariantUpdateDTO updateDto = new ProductVariantUpdateDTO();
    updateDto.setIsAvailable(false);

    when(productVariantService.updateProductVariant(any(), any()))
        .thenThrow(
            new ResourceNotFoundException("ProductVariant not found with id: " + nonExistentId));

    mockMvc
        .perform(
            put("/api/v1/product-variants/" + nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldThrowUnauthorizedException_WhenUpdatingVariant() throws Exception {
    ProductVariantUpdateDTO updateDto = new ProductVariantUpdateDTO();
    updateDto.setIsAvailable(false);

    mockMvc
        .perform(
            put("/api/v1/product-variants/" + variantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser
  public void shouldDeleteProductVariant() throws Exception {
    doNothing().when(productVariantService).deleteProductVariant(variantId);

    mockMvc
        .perform(delete("/api/v1/product-variants/" + variantId))
        .andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser
  public void shouldThrowException_WhenDeletingNonExistentVariant() throws Exception {
    UUID nonExistentId = UUID.randomUUID();
    doThrow(new ResourceNotFoundException("ProductVariant not found with id: " + nonExistentId))
        .when(productVariantService)
        .deleteProductVariant(nonExistentId);

    mockMvc
        .perform(delete("/api/v1/product-variants/" + nonExistentId))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldThrowUnauthorizedException_WhenDeletingVariant() throws Exception {
    mockMvc
        .perform(delete("/api/v1/product-variants/" + variantId))
        .andExpect(status().isForbidden());
  }
}
