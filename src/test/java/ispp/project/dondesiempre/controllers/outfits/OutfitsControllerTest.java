package ispp.project.dondesiempre.controllers.outfits;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import ispp.project.dondesiempre.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.models.outfits.Outfit;
import ispp.project.dondesiempre.models.outfits.OutfitProduct;
import ispp.project.dondesiempre.models.outfits.dto.OutfitCreationDTO;
import ispp.project.dondesiempre.models.outfits.dto.OutfitCreationProductDTO;
import ispp.project.dondesiempre.models.outfits.dto.OutfitUpdateDTO;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.models.products.ProductType;
import ispp.project.dondesiempre.models.storefronts.Storefront;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.services.outfits.OutfitService;
import ispp.project.dondesiempre.services.products.ProductService;
import ispp.project.dondesiempre.services.storefronts.StorefrontService;
import ispp.project.dondesiempre.services.stores.StoreService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = OutfitController.class)
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
  void create_shouldReturnCreated_whenValidDTO() throws Exception {
    OutfitCreationProductDTO productDTO = new OutfitCreationProductDTO();
    productDTO.setId(productId);
    productDTO.setIndex(0);

    OutfitCreationDTO creationDTO = new OutfitCreationDTO();
    creationDTO.setName("New Outfit");
    creationDTO.setIndex(0);
    creationDTO.setStorefrontId(storefrontId);
    creationDTO.setTags(List.of(TEST_TAG));
    creationDTO.setProducts(List.of(productDTO));

    when(outfitService.create(any())).thenReturn(outfit);
    when(outfitService.findTagsByOutfitId(outfitId)).thenReturn(List.of(TEST_TAG));
    when(outfitService.findOutfitProductsByOutfitId(outfitId)).thenReturn(List.of(outfitProduct));

    mockMvc
        .perform(
            post("/api/v1/outfits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(creationDTO)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(outfitId.toString()))
        .andExpect(jsonPath("$.name").value(outfit.getName()));
  }

  @Test
  void create_shouldReturnBadRequest_whenNoProducts() throws Exception {
    OutfitCreationDTO creationDTO = new OutfitCreationDTO();
    creationDTO.setName("New Outfit");
    creationDTO.setIndex(0);
    creationDTO.setStorefrontId(storefrontId);
    creationDTO.setTags(List.of());
    creationDTO.setProducts(List.of());

    when(outfitService.create(any()))
        .thenThrow(new InvalidRequestException("An outfit cannot be created without products."));

    mockMvc
        .perform(
            post("/api/v1/outfits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(creationDTO)))
        .andExpect(status().isBadRequest());
  }

  // -------------------------------------------------------------------------
  // PUT /api/v1/outfits/{id}
  // -------------------------------------------------------------------------

  @Test
  void update_shouldReturnOk_whenOutfitExists() throws Exception {
    OutfitUpdateDTO updateDTO = new OutfitUpdateDTO();
    updateDTO.setName("Updated Outfit");
    updateDTO.setDiscountedPriceInCents(2000);
    updateDTO.setIndex(1);

    when(outfitService.update(eq(outfitId), any())).thenReturn(outfit);
    when(outfitService.findTagsByOutfitId(outfitId)).thenReturn(List.of());
    when(outfitService.findOutfitProductsByOutfitId(outfitId)).thenReturn(List.of(outfitProduct));

    mockMvc
        .perform(
            put("/api/v1/outfits/" + outfitId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(outfitId.toString()));
  }

  @Test
  void update_shouldReturnNotFound_whenOutfitDoesNotExist() throws Exception {
    UUID nonExistentId = UUID.randomUUID();
    OutfitUpdateDTO updateDTO = new OutfitUpdateDTO();
    updateDTO.setName("Updated Outfit");
    updateDTO.setDiscountedPriceInCents(2000);
    updateDTO.setIndex(1);

    when(outfitService.update(eq(nonExistentId), any()))
        .thenThrow(new ResourceNotFoundException("Outfit not found."));

    mockMvc
        .perform(
            put("/api/v1/outfits/" + nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
        .andExpect(status().isNotFound());
  }

  // -------------------------------------------------------------------------
  // POST /api/v1/outfits/{id}/tags
  // -------------------------------------------------------------------------

  @Test
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

  @Test
  void addTag_shouldReturnNotFound_whenOutfitDoesNotExist() throws Exception {
    UUID nonExistentId = UUID.randomUUID();
    when(outfitService.addTag(eq(nonExistentId), any()))
        .thenThrow(new ResourceNotFoundException("Outfit not found."));

    mockMvc
        .perform(
            post("/api/v1/outfits/" + nonExistentId + "/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"" + TEST_TAG + "\""))
        .andExpect(status().isNotFound());
  }

  // -------------------------------------------------------------------------
  // DELETE /api/v1/outfits/{id}/tags
  // -------------------------------------------------------------------------

  @Test
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

  @Test
  void removeTag_shouldReturnNotFound_whenOutfitDoesNotExist() throws Exception {
    UUID nonExistentId = UUID.randomUUID();
    doNothing().when(outfitService).removeTag(eq(nonExistentId), any());
    // Si el servicio lanza excepción al no encontrar el outfit
    org.mockito.Mockito.doThrow(new ResourceNotFoundException("Outfit not found."))
        .when(outfitService)
        .removeTag(eq(nonExistentId), any());

    mockMvc
        .perform(
            delete("/api/v1/outfits/" + nonExistentId + "/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"" + TEST_TAG + "\""))
        .andExpect(status().isNotFound());
  }

  // -------------------------------------------------------------------------
  // POST /api/v1/outfits/{id}/products
  // -------------------------------------------------------------------------

  @Test
  void addProduct_shouldReturnCreated_whenValid() throws Exception {
    OutfitCreationProductDTO productDTO = new OutfitCreationProductDTO();
    productDTO.setId(productId);
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

  @Test
  void addProduct_shouldReturnBadRequest_whenProductFromDifferentStore() throws Exception {
    OutfitCreationProductDTO productDTO = new OutfitCreationProductDTO();
    productDTO.setId(productId);
    productDTO.setIndex(0);

    when(outfitService.addProduct(eq(outfitId), any()))
        .thenThrow(
            new InvalidRequestException(
                "All products in an outfit must belong to the same store."));

    mockMvc
        .perform(
            post("/api/v1/outfits/" + outfitId + "/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)))
        .andExpect(status().isBadRequest());
  }

  // -------------------------------------------------------------------------
  // DELETE /api/v1/outfits/{id}/products/{productId}
  // -------------------------------------------------------------------------

  @Test
  void removeProduct_shouldReturnCreated_whenValid() throws Exception {
    when(productService.getProductById(productId)).thenReturn(product);
    doNothing().when(outfitService).removeProduct(eq(outfitId), eq(product));

    mockMvc
        .perform(delete("/api/v1/outfits/" + outfitId + "/products/" + productId))
        .andExpect(status().isCreated())
        .andExpect(content().string("Product successfully removed from outfit."));
  }

  @Test
  void removeProduct_shouldReturnNotFound_whenProductDoesNotExist() throws Exception {
    UUID nonExistentProductId = UUID.randomUUID();
    when(productService.getProductById(nonExistentProductId))
        .thenThrow(new ResourceNotFoundException("Product not found."));

    mockMvc
        .perform(delete("/api/v1/outfits/" + outfitId + "/products/" + nonExistentProductId))
        .andExpect(status().isNotFound());
  }

  // -------------------------------------------------------------------------
  // PATCH /api/v1/outfits/{id}/products/sort
  // -------------------------------------------------------------------------

  @Test
  void sortProducts_shouldReturnOk_whenValid() throws Exception {
    OutfitCreationProductDTO p1 = new OutfitCreationProductDTO();
    p1.setId(productId);
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
  void delete_shouldReturnOk_whenOutfitExists() throws Exception {
    doNothing().when(outfitService).delete(outfitId);

    mockMvc
        .perform(delete("/api/v1/outfits/" + outfitId))
        .andExpect(status().isOk())
        .andExpect(content().string("Outfit successfully deleted."));
  }

  @Test
  void delete_shouldReturnNotFound_whenOutfitDoesNotExist() throws Exception {
    UUID nonExistentId = UUID.randomUUID();
    org.mockito.Mockito.doThrow(new ResourceNotFoundException("Outfit not found."))
        .when(outfitService)
        .delete(nonExistentId);

    mockMvc.perform(delete("/api/v1/outfits/" + nonExistentId)).andExpect(status().isNotFound());
  }
}
