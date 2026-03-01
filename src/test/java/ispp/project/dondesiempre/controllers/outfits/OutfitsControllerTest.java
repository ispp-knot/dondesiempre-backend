package ispp.project.dondesiempre.controllers.outfits;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import ispp.project.dondesiempre.models.outfits.dto.OutfitDTO;
import ispp.project.dondesiempre.models.outfits.dto.OutfitProductDTO;
import ispp.project.dondesiempre.models.outfits.dto.OutfitUpdateDTO;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.models.storefronts.Storefront;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.services.outfits.OutfitService;
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

  @MockitoBean private OutfitService outfitService;
  @MockitoBean private StoreService storeService;

  private UUID outfitId;
  private UUID storeId;
  private Store store;
  private OutfitDTO outfitDTO;
  private OutfitProductDTO outfitProductDTO;

  @BeforeEach
  void setUp() {
    outfitId = UUID.randomUUID();
    storeId = UUID.randomUUID();
    UUID storefrontId = UUID.randomUUID();

    store = new Store();
    store.setId(storeId);

    Storefront storefront = new Storefront();
    storefront.setId(storefrontId);

    Outfit outfit = new Outfit();
    outfit.setId(outfitId);
    outfit.setName("Test Outfit");
    outfit.setDescription("Test Description");
    outfit.setIndex(0);
    outfit.setDiscountedPriceInCents(1000);
    outfit.setStorefront(storefront);

    outfitDTO = new OutfitDTO(outfit, List.of("casual"), List.of());

    Store productStore = new Store();
    productStore.setId(UUID.randomUUID());

    Product product = new Product();
    product.setId(UUID.randomUUID());
    product.setName("Test Product");
    product.setDiscountedPriceInCents(500);
    product.setStore(productStore);

    OutfitProduct outfitProduct = new OutfitProduct();
    outfitProduct.setId(UUID.randomUUID());
    outfitProduct.setIndex(0);
    outfitProduct.setProduct(product);
    outfitProduct.setOutfit(outfit);

    outfitProductDTO = new OutfitProductDTO(outfitProduct);
  }

  // --- GET /api/v1/outfits/{id} ---

  @Test
  void shouldReturnOk_whenOutfitExists() throws Exception {
    when(outfitService.findByIdToDTO(outfitId)).thenReturn(outfitDTO);

    mockMvc
        .perform(get("/api/v1/outfits/" + outfitId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(outfitId.toString()))
        .andExpect(jsonPath("$.name").value("Test Outfit"));
  }

  @Test
  void shouldReturnNotFound_whenOutfitDoesNotExist() throws Exception {
    UUID nonExistentId = UUID.randomUUID();
    when(outfitService.findByIdToDTO(nonExistentId))
        .thenThrow(new ResourceNotFoundException("Outfit not found."));

    mockMvc.perform(get("/api/v1/outfits/" + nonExistentId)).andExpect(status().isNotFound());
  }

  // --- GET /api/v1/stores/{storeId}/outfits ---

  @Test
  void shouldReturnOk_whenStoreExists() throws Exception {
    when(storeService.findById(storeId)).thenReturn(store);
    when(outfitService.findByStore(store)).thenReturn(List.of(outfitDTO));

    mockMvc
        .perform(get("/api/v1/stores/" + storeId + "/outfits"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1))
        .andExpect(jsonPath("$[0].name").value("Test Outfit"));
  }

  @Test
  void shouldReturnNotFound_whenStoreDoesNotExist() throws Exception {
    UUID nonExistentId = UUID.randomUUID();
    when(storeService.findById(nonExistentId))
        .thenThrow(new ResourceNotFoundException("Store not found."));

    mockMvc
        .perform(get("/api/v1/stores/" + nonExistentId + "/outfits"))
        .andExpect(status().isNotFound());
  }

  // --- POST /api/v1/outfits ---

  @Test
  void shouldReturnCreated_whenCreatingOutfit() throws Exception {
    OutfitCreationDTO creationDTO = new OutfitCreationDTO();
    creationDTO.setName("New Outfit");
    creationDTO.setIndex(0);
    creationDTO.setStorefrontId(UUID.randomUUID());
    creationDTO.setTags(List.of());
    creationDTO.setProducts(List.of());

    when(outfitService.create(any())).thenReturn(outfitDTO);

    mockMvc
        .perform(
            post("/api/v1/outfits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(creationDTO)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(outfitId.toString()));
  }

  @Test
  void shouldReturnBadRequest_whenCreatingOutfitWithoutProducts() throws Exception {
    OutfitCreationDTO creationDTO = new OutfitCreationDTO();
    creationDTO.setName("New Outfit");

    when(outfitService.create(any()))
        .thenThrow(new InvalidRequestException("An outfit cannot be created without products."));

    mockMvc
        .perform(
            post("/api/v1/outfits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(creationDTO)))
        .andExpect(status().isBadRequest());
  }

  // --- PUT /api/v1/outfits/{id} ---

  @Test
  void shouldReturnOk_whenUpdatingOutfit() throws Exception {
    OutfitUpdateDTO updateDTO = new OutfitUpdateDTO();
    updateDTO.setName("Updated Outfit");
    updateDTO.setDiscountedPriceInCents(2000);

    when(outfitService.update(eq(outfitId), any())).thenReturn(outfitDTO);

    mockMvc
        .perform(
            put("/api/v1/outfits/" + outfitId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(outfitId.toString()));
  }

  @Test
  void shouldReturnNotFound_whenUpdatingNonExistentOutfit() throws Exception {
    UUID nonExistentId = UUID.randomUUID();
    OutfitUpdateDTO updateDTO = new OutfitUpdateDTO();
    updateDTO.setName("Updated Outfit");

    when(outfitService.update(eq(nonExistentId), any()))
        .thenThrow(new ResourceNotFoundException("Outfit not found."));

    mockMvc
        .perform(
            put("/api/v1/outfits/" + nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
        .andExpect(status().isNotFound());
  }

  // --- POST /api/v1/outfits/{id}/tags ---

  @Test
  void shouldReturnCreated_whenAddingTag() throws Exception {
    when(outfitService.addTag(eq(outfitId), any())).thenReturn("casual");

    mockMvc
        .perform(
            post("/api/v1/outfits/" + outfitId + "/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"casual\""))
        .andExpect(status().isCreated());
  }

  // --- POST /api/v1/outfits/{id}/products ---

  @Test
  void shouldReturnCreated_whenAddingProduct() throws Exception {
    OutfitCreationProductDTO productDTO = new OutfitCreationProductDTO();
    productDTO.setId(UUID.randomUUID());
    productDTO.setIndex(0);

    when(outfitService.addProduct(eq(outfitId), any())).thenReturn(outfitProductDTO);

    mockMvc
        .perform(
            post("/api/v1/outfits/" + outfitId + "/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Test Product"));
  }

  // --- DELETE /api/v1/outfits/{id} ---

  @Test
  void shouldReturnOk_whenDeletingOutfit() throws Exception {
    mockMvc
        .perform(delete("/api/v1/outfits/" + outfitId))
        .andExpect(status().isOk())
        .andExpect(content().string("Outfit successfully deleted."));
  }
}
