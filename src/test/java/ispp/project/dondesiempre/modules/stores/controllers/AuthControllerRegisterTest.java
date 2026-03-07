package ispp.project.dondesiempre.modules.stores.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import ispp.project.dondesiempre.config.JwtProperties;
import ispp.project.dondesiempre.modules.auth.controllers.AuthController;
import ispp.project.dondesiempre.modules.auth.dtos.RegisterClientDTO;
import ispp.project.dondesiempre.modules.auth.dtos.RegisterStoreDTO;
import ispp.project.dondesiempre.modules.auth.services.AuthService;
import ispp.project.dondesiempre.modules.auth.services.UserService;
import ispp.project.dondesiempre.modules.clients.dtos.ClientDTO;
import ispp.project.dondesiempre.modules.common.exceptions.AlreadyExistsException;
import ispp.project.dondesiempre.modules.stores.dtos.StoreDTO;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.services.StoreService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
class AuthControllerRegisterTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private AuthService authService;
  @MockitoBean private UserService userService;
  @MockitoBean private StoreService storeService;
  @MockitoBean private JwtProperties jwtProperties;

  // --- registerStore ---

  @Test
  void registerStore_shouldReturn201AndStoreDTO_whenDataIsValid() throws Exception {
    Store store = new Store();
    store.setId(UUID.randomUUID());

    StoreDTO storeDTO = new StoreDTO();
    storeDTO.setId(store.getId());
    storeDTO.setName("Test Store");

    when(userService.registerStore(any())).thenReturn(store);
    when(storeService.toDTO(any())).thenReturn(storeDTO);

    mockMvc
        .perform(
            post("/api/v1/auth/register/store")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildStoreDTORequest("store@test.com"))))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Test Store"))
        .andExpect(jsonPath("$.id").isNotEmpty());
  }

  @Test
  void registerStore_shouldReturn409_whenEmailAlreadyExists() throws Exception {
    when(userService.registerStore(any()))
        .thenThrow(new AlreadyExistsException("Email already in use."));

    mockMvc
        .perform(
            post("/api/v1/auth/register/store")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildStoreDTORequest("taken@test.com"))))
        .andExpect(status().isConflict());
  }

  // --- registerClient ---

  @Test
  void registerClient_shouldReturn201AndClientDTO_whenDataIsValid() throws Exception {
    ClientDTO clientDTO = new ClientDTO();
    clientDTO.setId(UUID.randomUUID());
    clientDTO.setName("John");
    clientDTO.setSurname("Doe");
    clientDTO.setEmail("client@test.com");
    clientDTO.setPhone("+34600000000");
    clientDTO.setAddress("Test Street 1");

    when(userService.registerClient(any())).thenReturn(clientDTO);

    mockMvc
        .perform(
            post("/api/v1/auth/register/client")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildClientDTORequest("client@test.com"))))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("John"))
        .andExpect(jsonPath("$.surname").value("Doe"))
        .andExpect(jsonPath("$.email").value("client@test.com"))
        .andExpect(jsonPath("$.id").isNotEmpty());
  }

  @Test
  void registerClient_shouldReturn409_whenEmailAlreadyExists() throws Exception {
    when(userService.registerClient(any()))
        .thenThrow(new AlreadyExistsException("Email already in use."));

    mockMvc
        .perform(
            post("/api/v1/auth/register/client")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildClientDTORequest("taken@test.com"))))
        .andExpect(status().isConflict());
  }

  // --- password strength ---

  @Test
  void registerStore_shouldReturn400_whenPasswordIsWeak() throws Exception {
    RegisterStoreDTO dto = buildStoreDTORequest("store@test.com");
    dto.setPassword("weak");

    mockMvc
        .perform(
            post("/api/v1/auth/register/store")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void registerClient_shouldReturn400_whenPasswordIsWeak() throws Exception {
    RegisterClientDTO dto = buildClientDTORequest("client@test.com");
    dto.setPassword("weak");

    mockMvc
        .perform(
            post("/api/v1/auth/register/client")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isBadRequest());
  }

  // --- helpers ---

  private RegisterStoreDTO buildStoreDTORequest(String email) {
    RegisterStoreDTO dto = new RegisterStoreDTO();
    dto.setEmail(email);
    dto.setPassword("Password1!");
    dto.setName("Test Store");
    dto.setStoreID("B12345678");
    dto.setLatitude(40.416775);
    dto.setLongitude(-3.703790);
    dto.setAddress("Gran Vía 1, Madrid");
    dto.setOpeningHours("Mon-Fri 9-18");
    dto.setAcceptsShipping(true);
    dto.setPhone("+34600000000");
    dto.setAboutUs("A great store.");
    dto.setPrimaryColor("#c65a3a");
    dto.setSecondaryColor("#19756a");
    return dto;
  }

  private RegisterClientDTO buildClientDTORequest(String email) {
    RegisterClientDTO dto = new RegisterClientDTO();
    dto.setEmail(email);
    dto.setPassword("Password1!");
    dto.setName("John");
    dto.setSurname("Doe");
    dto.setPhone("+34600000000");
    dto.setAddress("Test Street 1");
    return dto;
  }
}
