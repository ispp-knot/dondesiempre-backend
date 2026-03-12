package ispp.project.dondesiempre.modules.outfits.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import ispp.project.dondesiempre.config.GlobalExceptionHandler;
import ispp.project.dondesiempre.modules.outfits.dtos.OutfitCreationDTO;
import ispp.project.dondesiempre.modules.outfits.dtos.OutfitCreationProductDTO;
import ispp.project.dondesiempre.modules.outfits.dtos.OutfitUpdateDTO;
import ispp.project.dondesiempre.modules.outfits.models.Outfit;
import ispp.project.dondesiempre.modules.outfits.models.OutfitProduct;
import ispp.project.dondesiempre.modules.outfits.services.OutfitService;
import ispp.project.dondesiempre.modules.products.models.Product;
import ispp.project.dondesiempre.modules.products.models.ProductType;
import ispp.project.dondesiempre.modules.products.services.ProductService;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.models.Storefront;
import ispp.project.dondesiempre.modules.stores.services.StoreService;
import ispp.project.dondesiempre.modules.stores.services.StorefrontService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockPart;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    controllers = OutfitController.class,
    excludeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {GlobalExceptionHandler.class}))
class OutfitsControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  // --- Todas las dependencias del controlador deben estar mockeadas ---
  @MockitoBean private OutfitService outfitService;
  @MockitoBean private ProductService productService;
  @MockitoBean private StoreService storeService;
  @MockitoBean private StorefrontService storefrontService;

  private UUID outfitId;
  private UUID storeId;
  private UUID storefrontId;
  private UUID productId;

  private Store store;
  private Storefront storefront;
  private Outfit outfit;
  private OutfitProduct outfitProduct;
  private Product product;

  private final String TEST_TAG = "casual";

  @BeforeEach
  void setUp() {
    outfitId = UUID.randomUUID();
    storeId = UUID.randomUUID();
    storefrontId = UUID.randomUUID();
    productId = UUID.randomUUID();

    storefront = new Storefront();
    storefront.setId(storefrontId);

    store = new Store();
    store.setId(storeId);
    store.setStorefront(storefront);

    outfit = new Outfit();
    outfit.setId(outfitId);
    outfit.setName("Test Outfit");
    outfit.setDescription("Test Description");
    outfit.setIndex(0);
    outfit.setDiscountedPriceInCents(1000);
    outfit.setStorefront(storefront);

    ProductType productType = new ProductType();
    productType.setId(UUID.randomUUID());
    productType.setType("Camiseta");

    product = new Product();
    product.setId(productId);
    product.setName("Test Product");
    product.setPriceInCents(600);
    product.setDiscountedPriceInCents(500);
    product.setStore(store);
    product.setType(productType);

    outfitProduct = new OutfitProduct();
    outfitProduct.setId(UUID.randomUUID());
    outfitProduct.setIndex(0);
    outfitProduct.setProduct(product);
    outfitProduct.setOutfit(outfit);
  }

  // -------------------------------------------------------------------------
  // GET /api/v1/outfits/{id}
  // -------------------------------------------------------------------------

  @Test
  void getById_shouldReturnOk_whenOutfitExists() throws Exception {
    when(outfitService.findById(outfitId)).thenReturn(outfit);
    when(outfitService.findTagsByOutfitId(outfitId)).thenReturn(List.of(TEST_TAG));
    when(outfitService.findOutfitProductsByOutfitId(outfitId)).thenReturn(List.of(outfitProduct));

    mockMvc
        .perform(get("/api/v1/outfits/" + outfitId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(outfitId.toString()))
        .andExpect(jsonPath("$.name").value(outfit.getName()));
  }

  // -------------------------------------------------------------------------
  // GET /api/v1/stores/{storeId}/outfits
  // -------------------------------------------------------------------------

  @Test
  void getByStoreId_shouldReturnOk_whenStoreExists() throws Exception {
    when(storeService.findById(storeId)).thenReturn(store);
    when(outfitService.findByStore(store)).thenReturn(List.of(outfit));
    when(outfitService.findTagsByOutfitId(outfitId)).thenReturn(List.of());
    when(outfitService.findOutfitProductsByOutfitId(outfitId)).thenReturn(List.of(outfitProduct));

    mockMvc
        .perform(get("/api/v1/stores/" + storeId + "/outfits"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1))
        .andExpect(jsonPath("$[0].name").value(outfit.getName()));
  }

  // -------------------------------------------------------------------------
  // GET /api/v1/storefronts/{storefrontId}/outfits
  // -------------------------------------------------------------------------

  @Test
  void getByStorefrontId_shouldReturnOk_whenStorefrontExists() throws Exception {
    when(storefrontService.findById(storefrontId)).thenReturn(storefront);
    when(outfitService.findByStorefront(storefront)).thenReturn(List.of(outfit));
    when(outfitService.findTagsByOutfitId(outfitId)).thenReturn(List.of());
    when(outfitService.findOutfitProductsByOutfitId(outfitId)).thenReturn(List.of(outfitProduct));

    mockMvc
        .perform(get("/api/v1/storefronts/" + storefrontId + "/outfits"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1));
  }

  // -------------------------------------------------------------------------
  // POST /api/v1/outfits
  // -------------------------------------------------------------------------

  @Test
  @WithMockUser
  void create_shouldReturnCreated_whenValidDTO() throws Exception {
    OutfitCreationProductDTO productDTO = new OutfitCreationProductDTO();
    productDTO.setProductId(productId);
    productDTO.setIndex(0);

    OutfitCreationDTO creationDTO = new OutfitCreationDTO();
    creationDTO.setName("New Outfit");
    creationDTO.setIndex(0);
    creationDTO.setTags(List.of(TEST_TAG));
    creationDTO.setProducts(List.of(productDTO));

    when(outfitService.create(any(), any(), any())).thenReturn(outfit);
    when(outfitService.findTagsByOutfitId(outfitId)).thenReturn(List.of(TEST_TAG));
    when(outfitService.findOutfitProductsByOutfitId(outfitId)).thenReturn(List.of(outfitProduct));

    MockPart dtoPart = new MockPart("dto", objectMapper.writeValueAsBytes(creationDTO));
    dtoPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

    mockMvc
        .perform(multipart("/api/v1/outfits?storefrontId=" + storefrontId).part(dtoPart))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(outfitId.toString()))
        .andExpect(jsonPath("$.name").value(outfit.getName()));
  }

  // -------------------------------------------------------------------------
  // PUT /api/v1/outfits/{id}
  // -------------------------------------------------------------------------

  @Test
  @WithMockUser
  void update_shouldReturnOk_whenOutfitExists() throws Exception {
    OutfitUpdateDTO updateDTO = new OutfitUpdateDTO();
    updateDTO.setName("Updated Outfit");
    updateDTO.setDiscountedPriceInCents(2000);
    updateDTO.setIndex(1);

    when(outfitService.update(eq(outfitId), any(), any())).thenReturn(outfit);
    when(outfitService.findTagsByOutfitId(outfitId)).thenReturn(List.of());
    when(outfitService.findOutfitProductsByOutfitId(outfitId)).thenReturn(List.of(outfitProduct));

    MockPart dtoPart = new MockPart("dto", objectMapper.writeValueAsBytes(updateDTO));
    dtoPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

    mockMvc
        .perform(multipart(HttpMethod.PUT, "/api/v1/outfits/" + outfitId).part(dtoPart))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(outfitId.toString()));
  }

  // -------------------------------------------------------------------------
  // POST /api/v1/outfits/{id}/tags
  // -------------------------------------------------------------------------

  @Test
  @WithMockUser
  void addTag_shouldReturnCreated_whenOutfitExists() throws Exception {
    when(outfitService.addTag(eq(outfitId), any())).thenReturn(TEST_TAG);

    mockMvc
        .perform(
            post("/api/v1/outfits/" + outfitId + "/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"" + TEST_TAG + "\""))
        .andExpect(status().isCreated())
        .andExpect(content().string(TEST_TAG));
  }

  // -------------------------------------------------------------------------
  // DELETE /api/v1/outfits/{id}/tags
  // -------------------------------------------------------------------------

  @Test
  @WithMockUser
  void removeTag_shouldReturnOk_whenTagExists() throws Exception {
    doNothing().when(outfitService).removeTag(eq(outfitId), any());

    mockMvc
        .perform(
            delete("/api/v1/outfits/" + outfitId + "/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"" + TEST_TAG + "\""))
        .andExpect(status().isOk())
        .andExpect(content().string("Tag successfully removed from outfit."));
  }

  // -------------------------------------------------------------------------
  // POST /api/v1/outfits/{id}/products
  // -------------------------------------------------------------------------

  @Test
  @WithMockUser
  void addProduct_shouldReturnCreated_whenValid() throws Exception {
    OutfitCreationProductDTO productDTO = new OutfitCreationProductDTO();
    productDTO.setProductId(productId);
    productDTO.setIndex(0);

    when(outfitService.addProduct(eq(outfitId), any())).thenReturn(outfitProduct);

    mockMvc
        .perform(
            post("/api/v1/outfits/" + outfitId + "/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Test Product"));
  }

  // -------------------------------------------------------------------------
  // DELETE /api/v1/outfits/{id}/products/{productId}
  // -------------------------------------------------------------------------

  @Test
  @WithMockUser
  void removeProduct_shouldReturnCreated_whenValid() throws Exception {
    when(productService.getProductById(productId)).thenReturn(product);
    doNothing().when(outfitService).removeProduct(eq(outfitId), eq(product));

    mockMvc
        .perform(delete("/api/v1/outfits/" + outfitId + "/products/" + productId))
        .andExpect(status().isCreated())
        .andExpect(content().string("Product successfully removed from outfit."));
  }

  // -------------------------------------------------------------------------
  // PATCH /api/v1/outfits/{id}/products/sort
  // -------------------------------------------------------------------------

  @Test
  @WithMockUser
  void sortProducts_shouldReturnOk_whenValid() throws Exception {
    OutfitCreationProductDTO p1 = new OutfitCreationProductDTO();
    p1.setProductId(productId);
    p1.setIndex(0);

    doNothing().when(outfitService).sortProducts(eq(outfitId), any());

    mockMvc
        .perform(
            patch("/api/v1/outfits/" + outfitId + "/products/sort")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(p1))))
        .andExpect(status().isOk())
        .andExpect(content().string("Products successfully sorted."));
  }

  // -------------------------------------------------------------------------
  // DELETE /api/v1/outfits/{id}
  // -------------------------------------------------------------------------

  @Test
  @WithMockUser
  void delete_shouldReturnOk_whenOutfitExists() throws Exception {
    doNothing().when(outfitService).delete(outfitId);

    mockMvc
        .perform(delete("/api/v1/outfits/" + outfitId))
        .andExpect(status().isOk())
        .andExpect(content().string("Outfit successfully deleted."));
  }
}
