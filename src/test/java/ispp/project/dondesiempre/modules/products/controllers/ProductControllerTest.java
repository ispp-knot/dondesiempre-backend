package ispp.project.dondesiempre.modules.products.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import ispp.project.dondesiempre.config.GlobalExceptionHandler;
import ispp.project.dondesiempre.modules.outfits.services.OutfitService;
import ispp.project.dondesiempre.modules.products.dtos.ProductCreationDTO;
import ispp.project.dondesiempre.modules.products.dtos.ProductDiscountUpdateDTO;
import ispp.project.dondesiempre.modules.products.dtos.ProductUpdateDTO;
import ispp.project.dondesiempre.modules.products.models.Product;
import ispp.project.dondesiempre.modules.products.models.ProductType;
import ispp.project.dondesiempre.modules.products.services.ProductService;
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
import org.springframework.mock.web.MockPart;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    controllers = ProductController.class,
    excludeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {GlobalExceptionHandler.class}))
public class ProductControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private ProductService productService;
  @MockitoBean private OutfitService outfitService;

  private UUID productId;
  private UUID storeId;
  private UUID productTypeId;

  private Store store;
  private Product product;
  private ProductType productType;

  @BeforeEach
  void setUp() {
    productId = UUID.randomUUID();
    storeId = UUID.randomUUID();
    productTypeId = UUID.randomUUID();

    store = new Store();
    store.setId(storeId);

    productType = new ProductType();
    productType.setId(productTypeId);
    productType.setType("Test Type");

    product = new Product();
    product.setId(productId);
    product.setName("Test Product");
    product.setPriceInCents(1000);
    product.setDescription("This is a test product");
    product.setType(productType);
    product.setStore(store);
  }

  @Test
  @WithMockUser
  public void shouldCreateNewProduct() throws Exception {
    ProductCreationDTO dto = new ProductCreationDTO();
    dto.setName("Test Product");
    dto.setPriceInCents(1000);
    dto.setDescription("This is a test product");
    dto.setTypeId(productTypeId);

    when(productService.createProduct(any(), any(), any())).thenReturn(product);

    MockPart dtoPart = new MockPart("dto", objectMapper.writeValueAsBytes(dto));
    dtoPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

    mockMvc
        .perform(multipart("/api/v1/products").part(dtoPart).param("storeId", storeId.toString()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(productId.toString()))
        .andExpect(jsonPath("$.name").value(product.getName()));
  }

  @Test
  @WithMockUser
  public void shouldUpdateProductDiscount() throws Exception {
    ProductDiscountUpdateDTO discount = new ProductDiscountUpdateDTO();
    discount.setDiscountPercentage(70);

    product.setDiscountPercentage(70);

    when(productService.updateProductDiscount(any(), any())).thenReturn(product);

    mockMvc
        .perform(
            patch("/api/v1/products/" + productId + "/discount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(discount)))
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$.discountPercentage").value(70));
  }

  @Test
  public void shouldGetProductById() throws Exception {
    when(productService.getProductById(productId)).thenReturn(product);

    mockMvc
        .perform(get("/api/v1/products/" + productId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(productId.toString()))
        .andExpect(jsonPath("$.name").value(product.getName()));
  }

  @Test
  public void shouldGetAllProducts() throws Exception {
    when(productService.findAll()).thenReturn(List.of(product));

    mockMvc
        .perform(get("/api/v1/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1))
        .andExpect(jsonPath("$[0].name").value(product.getName()));
  }

  @Test
  public void shouldReturnEmptyList_whenGetAllProducts_noProductsExist() throws Exception {
    when(productService.findAll()).thenReturn(List.of());

    mockMvc
        .perform(get("/api/v1/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(0));
  }

  @Test
  public void shouldReturnMultipleProducts_whenGetAllProducts() throws Exception {
    Product product2 = new Product();
    product2.setId(UUID.randomUUID());
    product2.setName("Product 2");
    product2.setPriceInCents(2000);
    product2.setType(productType);
    product2.setStore(store);

    when(productService.findAll()).thenReturn(List.of(product, product2));

    mockMvc
        .perform(get("/api/v1/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(2))
        .andExpect(jsonPath("$[0].name").value(product.getName()))
        .andExpect(jsonPath("$[1].name").value(product2.getName()));
  }

  @Test
  public void shouldGetAllDiscountedProducts() throws Exception {
    product.setDiscountPercentage(10);
    when(productService.getAllDiscountedProducts()).thenReturn(List.of(product));

    mockMvc
        .perform(get("/api/v1/products/discounted"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1))
        .andExpect(jsonPath("$[0].name").value(product.getName()));
  }

  @Test
  public void shouldReturnProductsList_whenStoreHasProducts() throws Exception {
    when(productService.findByStoreIdAndNameContainingIgnoreCase(storeId, null))
        .thenReturn(List.of(product));

    mockMvc
        .perform(get("/api/v1/stores/" + storeId + "/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1))
        .andExpect(jsonPath("$[0].name").value(product.getName()));
  }

  @Test
  public void shouldReturnProductsList_whenFilteredByName() throws Exception {
    when(productService.findByStoreIdAndNameContainingIgnoreCase(storeId, "Test"))
        .thenReturn(List.of(product));

    mockMvc
        .perform(get("/api/v1/stores/" + storeId + "/products?name=Test"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1))
        .andExpect(jsonPath("$[0].name").value(product.getName()));
  }

  @Test
  public void shouldReturnEmptyList_whenStoreHasNoProducts() throws Exception {
    when(productService.findByStoreIdAndNameContainingIgnoreCase(storeId, null))
        .thenReturn(List.of());

    mockMvc
        .perform(get("/api/v1/stores/" + storeId + "/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(0));
  }

  @Test
  @WithMockUser
  public void shouldUpdateProduct() throws Exception {
    ProductUpdateDTO dto = new ProductUpdateDTO();
    dto.setName("Updated Product");

    product.setName("Updated Product");

    when(productService.updateProduct(any(), any(), any())).thenReturn(product);

    MockPart dtoPart = new MockPart("product", objectMapper.writeValueAsBytes(dto));
    dtoPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

    mockMvc
        .perform(
            multipart("/api/v1/products/" + productId)
                .part(dtoPart)
                .with(
                    request -> {
                      request.setMethod("PUT");
                      return request;
                    }))
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$.name").value("Updated Product"));
  }

  @Test
  @WithMockUser
  public void shouldDeleteProduct() throws Exception {
    doNothing().when(productService).deleteProduct(productId);

    mockMvc.perform(delete("/api/v1/products/" + productId)).andExpect(status().isOk());
  }
}
