package ispp.project.dondesiempre.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.models.User;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.repositories.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;

  @InjectMocks private UserService userService;

  @BeforeEach
  void injectSelf() {
    ReflectionTestUtils.setField(userService, "userService", userService);
  }

  @Test
  void getCurrentUser_shouldReturnUser_whenFound() {
    User user = new User();
    user.setId(1);
    user.setEmail(UserService.SEED_USER_EMAIL);
    when(userRepository.findByEmail(UserService.SEED_USER_EMAIL)).thenReturn(Optional.of(user));

    User result = userService.getCurrentUser();

    assertEquals(user, result);
  }

  @Test
  void getCurrentUser_shouldThrowResourceNotFoundException_whenNotFound() {
    when(userRepository.findByEmail(UserService.SEED_USER_EMAIL)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> userService.getCurrentUser());
  }

  @Test
  void assertUserOwnsStore_shouldNotThrow_whenUserOwnsStore() {
    User user = new User();
    user.setId(1);
    when(userRepository.findByEmail(UserService.SEED_USER_EMAIL)).thenReturn(Optional.of(user));

    Store store = new Store();
    store.setUser(user);

    assertDoesNotThrow(() -> userService.assertUserOwnsStore(store));
  }

  @Test
  void assertUserOwnsStore_shouldThrowUnauthorizedException_whenUserDoesNotOwnStore() {
    User currentUser = new User();
    currentUser.setId(1);
    when(userRepository.findByEmail(UserService.SEED_USER_EMAIL))
        .thenReturn(Optional.of(currentUser));

    User otherUser = new User();
    otherUser.setId(2);

    Store store = new Store();
    store.setUser(otherUser);

    assertThrows(UnauthorizedException.class, () -> userService.assertUserOwnsStore(store));
  }
}
