package ispp.project.dondesiempre.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import ispp.project.dondesiempre.models.User;
import ispp.project.dondesiempre.repositories.ClientRepository;
import ispp.project.dondesiempre.repositories.UserRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private ClientRepository clientRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private ApplicationContext applicationContext;

  @InjectMocks private UserService userService;

  @Test
  void register_shouldCreateUserWithHashedPassword() {
    when(passwordEncoder.encode(anyString())).thenReturn("hashed-password");
    User saved = new User();
    saved.setId(UUID.randomUUID());
    saved.setEmail("test@example.com");
    saved.setPassword("hashed-password");
    when(userRepository.save(any(User.class))).thenReturn(saved);

    User result = userService.register("test@example.com", "raw-password");

    assertNotNull(result.getId());
    assertEquals("test@example.com", result.getEmail());
    assertEquals("hashed-password", result.getPassword());
  }

  @Test
  void checkPassword_shouldReturnTrue_whenPasswordMatches() {
    User user = new User();
    user.setPassword("hashed-password");
    when(passwordEncoder.matches("raw-password", "hashed-password")).thenReturn(true);

    assertTrue(userService.checkPassword(user, "raw-password"));
  }

  @Test
  void checkPassword_shouldReturnFalse_whenPasswordDoesNotMatch() {
    User user = new User();
    user.setPassword("hashed-password");
    when(passwordEncoder.matches("wrong-password", "hashed-password")).thenReturn(false);

    assertFalse(userService.checkPassword(user, "wrong-password"));
  }
}
