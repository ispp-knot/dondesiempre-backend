package ispp.project.dondesiempre.modules.auth.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import ispp.project.dondesiempre.config.GlobalExceptionHandler;
import ispp.project.dondesiempre.modules.auth.dtos.LoginRequestDTO;
import ispp.project.dondesiempre.modules.auth.dtos.UserResponseDTO;
import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.auth.services.AuthService;
import ispp.project.dondesiempre.modules.auth.services.JwtService;
import ispp.project.dondesiempre.modules.auth.services.UserService;
import ispp.project.dondesiempre.modules.common.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.modules.payment.services.PaymentService;
import ispp.project.dondesiempre.modules.stores.services.StoreService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
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
    controllers = AuthController.class,
    excludeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {GlobalExceptionHandler.class}))
class AuthControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private AuthService authService;
  @MockitoBean private UserService userService;
  @MockitoBean private StoreService storeService;
  @MockitoBean private JwtService jwtService;
  @MockitoBean private PaymentService paymentService;

  private User createTestUser() {
    User user = new User();
    user.setId(UUID.randomUUID());
    user.setEmail("user@test.com");
    return user;
  }

  private UserResponseDTO createTestUserResponseDTO(User user) {
    return new UserResponseDTO(
        user.getId(), user.getEmail(), List.of(), Instant.now().plusSeconds(3600), null, null);
  }

  @Test
  void logIn_shouldReturn200AndTokenInBody_whenCredentialsAreValid() throws Exception {
    User user = createTestUser();
    UserResponseDTO responseDTO = createTestUserResponseDTO(user);

    when(authService.logIn("user@test.com", "password")).thenReturn(user);
    when(authService.getStoreId(user)).thenReturn(null);
    when(authService.getClientId(user)).thenReturn(null);
    when(jwtService.generateToken(eq("user@test.com"), isNull(), isNull())).thenReturn("jwt-token");
    when(authService.buildUserResponse(any(), any())).thenReturn(responseDTO);

    LoginRequestDTO dto = new LoginRequestDTO("user@test.com", "password");

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("jwt-token"))
        .andExpect(jsonPath("$.user.email").value("user@test.com"));
  }

  @Test
  void logIn_shouldReturn403_whenCredentialsAreInvalid() throws Exception {
    when(authService.logIn(any(), any()))
        .thenThrow(new UnauthorizedException("Invalid credentials."));

    LoginRequestDTO dto = new LoginRequestDTO("user@test.com", "wrong");

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser
  void me_shouldReturnUserInfo_whenAuthenticated() throws Exception {
    User user = createTestUser();
    UserResponseDTO responseDTO = createTestUserResponseDTO(user);

    when(authService.getCurrentUser()).thenReturn(user);
    when(authService.buildUserResponse(any(), any())).thenReturn(responseDTO);

    mockMvc
        .perform(get("/api/v1/auth/me").header("Authorization", "Bearer jwt-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("user@test.com"))
        .andExpect(jsonPath("$.id").isNotEmpty());
  }

  @Test
  @WithMockUser
  void me_shouldReturn403_whenServiceThrowsUnauthorized() throws Exception {
    when(authService.getCurrentUser()).thenThrow(new UnauthorizedException("Not authenticated."));

    mockMvc
        .perform(get("/api/v1/auth/me").header("Authorization", "Bearer jwt-token"))
        .andExpect(status().isForbidden());
  }
}
