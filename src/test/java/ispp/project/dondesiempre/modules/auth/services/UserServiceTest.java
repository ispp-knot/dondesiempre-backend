package ispp.project.dondesiempre.modules.auth.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.auth.repositories.UserRepository;
import ispp.project.dondesiempre.modules.clients.repositories.ClientRepository;
import ispp.project.dondesiempre.modules.common.exceptions.UnauthorizedException;
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
  @Mock private AuthService authService;

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

  @Test
  void changePassword_shouldChangePassword_whenOldPasswordIsCorrect() {
    User user = new User();
    user.setPassword("old-hashed-password");

    when(applicationContext.getBean(AuthService.class)).thenReturn(authService);
    when(authService.getCurrentUser()).thenReturn(user);
    when(passwordEncoder.matches("correct-old-password", "old-hashed-password")).thenReturn(true);
    when(passwordEncoder.encode("new-password")).thenReturn("new-hashed-password");

    userService.changePassword("correct-old-password", "new-password");

    assertEquals("new-hashed-password", user.getPassword(), "La contraseña del usuario debería haberse actualizado con el nuevo hash");
    verify(userRepository).save(user);
  }

  @Test
  void changePassword_shouldThrowException_whenOldPasswordIsIncorrect() {
    User user = new User();
    user.setPassword("old-hashed-password");

    when(applicationContext.getBean(AuthService.class)).thenReturn(authService);
    when(authService.getCurrentUser()).thenReturn(user);
    when(passwordEncoder.matches("wrong-old-password", "old-hashed-password")).thenReturn(false);

    assertThrows(UnauthorizedException.class,
            () -> userService.changePassword("wrong-old-password", "new-password")
    );

    verify(passwordEncoder, never()).encode(any());
    verify(userRepository, never()).save(any());
  }
}
