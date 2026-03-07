package ispp.project.dondesiempre;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import ispp.project.dondesiempre.modules.auth.dtos.LoginRequestDTO;
import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.auth.repositories.UserRepository;
import ispp.project.dondesiempre.utils.cloudinary.CloudinaryService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Transactional()
class AuthIntegrationTest {

  private static final String TEST_EMAIL = "auth-integration@test.com";
  private static final String TEST_PASSWORD = "integration-password";

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private UserRepository userRepository;
  @Autowired private PasswordEncoder passwordEncoder;
  @MockitoBean private CloudinaryService cloudinaryService;

  @BeforeEach
  void setUp() {
    userRepository.findByEmail(TEST_EMAIL).ifPresent(userRepository::delete);

    User user = new User();
    user.setEmail(TEST_EMAIL);
    user.setPassword(passwordEncoder.encode(TEST_PASSWORD));
    userRepository.save(user);
  }

  @AfterEach
  void tearDown() {
    userRepository.findByEmail(TEST_EMAIL).ifPresent(userRepository::delete);
  }

  @Test
  void logInAndGetMe_shouldReturnAuthenticatedUserInfo() throws Exception {
    LoginRequestDTO loginDTO = new LoginRequestDTO(TEST_EMAIL, TEST_PASSWORD);

    MvcResult loginResult =
        mockMvc
            .perform(
                post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginDTO)))
            .andExpect(status().isOk())
            .andReturn();

    String setCookieHeader = loginResult.getResponse().getHeader("Set-Cookie");
    String jwtToken = setCookieHeader.split(";")[0].split("=", 2)[1];

    mockMvc
        .perform(get("/api/v1/auth/me").cookie(new Cookie("token", jwtToken)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value(TEST_EMAIL))
        .andExpect(jsonPath("$.id").isNotEmpty());
  }
}
