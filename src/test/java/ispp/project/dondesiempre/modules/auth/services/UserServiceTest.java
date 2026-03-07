package ispp.project.dondesiempre.modules.auth.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.auth.repositories.UserRepository;
import ispp.project.dondesiempre.modules.clients.repositories.ClientRepository;
import ispp.project.dondesiempre.modules.stores.repositories.StoreRepository;
import ispp.project.dondesiempre.modules.stores.repositories.StorefrontRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private ClientRepository clientRepository;
  @Mock private StoreRepository storeRepository;
  @Mock private StorefrontRepository storefrontRepository;
  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private ApplicationContext applicationContext;

  @InjectMocks private UserService userService;

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
