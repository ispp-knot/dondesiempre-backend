package ispp.project.dondesiempre.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import ispp.project.dondesiempre.config.JwtProperties;
import ispp.project.dondesiempre.controllers.auth.AuthController;
import ispp.project.dondesiempre.controllers.auth.dto.LoginRequestDTO;
import ispp.project.dondesiempre.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.models.User;
import ispp.project.dondesiempre.services.AuthService;
import ispp.project.dondesiempre.services.UserService;
import ispp.project.dondesiempre.services.stores.StoreService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private AuthService authService;
  @MockitoBean private UserService userService;
  @MockitoBean private StoreService storeService;
  @MockitoBean private JwtProperties jwtProperties;

  @Test
  void logIn_shouldReturn200AndSetCookie_whenCredentialsAreValid() throws Exception {
    when(authService.logIn("user@test.com", "password")).thenReturn("jwt-token");
    when(jwtProperties.getDuration()).thenReturn(2592000L);

    LoginRequestDTO dto = new LoginRequestDTO("user@test.com", "password");

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(header().string("Set-Cookie", containsString("token=jwt-token")))
        .andExpect(header().string("Set-Cookie", containsString("HttpOnly")));
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
    User user = new User();
    user.setId(UUID.randomUUID());
    user.setEmail("user@test.com");
    when(authService.getCurrentUser()).thenReturn(user);

    mockMvc
        .perform(get("/api/v1/auth/me"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("user@test.com"))
        .andExpect(jsonPath("$.id").isNotEmpty());
  }

  @Test
  @WithMockUser
  void me_shouldReturn403_whenServiceThrowsUnauthorized() throws Exception {
    when(authService.getCurrentUser()).thenThrow(new UnauthorizedException("Not authenticated."));

    mockMvc.perform(get("/api/v1/auth/me")).andExpect(status().isForbidden());
  }
}
