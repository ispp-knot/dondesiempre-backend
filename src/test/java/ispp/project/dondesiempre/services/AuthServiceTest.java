package ispp.project.dondesiempre.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.auth.repositories.UserRepository;
import ispp.project.dondesiempre.modules.auth.services.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private UserService userService;
  @Mock
  private JwtService jwtService;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private ApplicationContext applicationContext;

  @InjectMocks
  private AuthService authService;

  @BeforeEach
  void setUp() {
    lenient().when(applicationContext.getBean(AuthService.class)).thenReturn(authService);
  }

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  // --- getCurrentUser ---

  @Test
  void getCurrentUser_shouldReturnUser_whenAuthenticated() {
    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken("user@test.com", null, List.of()));
    User user = new User();
    user.setEmail("user@test.com");
    when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

    User result = authService.getCurrentUser();

    assertEquals("user@test.com", result.getEmail());
  }

  @Test
  void getCurrentUser_shouldThrowUnauthorized_whenNoAuthentication() {
    SecurityContextHolder.clearContext();
    assertThrows(UnauthorizedException.class, () -> authService.getCurrentUser());
  }

  @Test
  void getCurrentUser_shouldThrowUnauthorized_whenAnonymous() {
    SecurityContextHolder.getContext()
        .setAuthentication(
            new AnonymousAuthenticationToken(
                "key", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")));
    assertThrows(UnauthorizedException.class, () -> authService.getCurrentUser());
  }

  @Test
  void getCurrentUser_shouldThrowResourceNotFound_whenUserMissingFromDB() {
    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken("ghost@test.com", null, List.of()));
    when(userRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> authService.getCurrentUser());
  }

  // --- assertUserOwnsStore ---

  @Test
  void assertUserOwnsStore_shouldNotThrow_whenCurrentUserOwnsStore() {
    User user = new User();
    user.setId(UUID.randomUUID());
    user.setEmail("user@test.com");
    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken("user@test.com", null, List.of()));
    when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

    Store store = new Store();
    store.setUser(user);

    assertDoesNotThrow(() -> authService.assertUserOwnsStore(store));
  }

  @Test
  void assertUserOwnsStore_shouldThrowUnauthorized_whenCurrentUserDoesNotOwnStore() {
    User currentUser = new User();
    currentUser.setId(UUID.randomUUID());
    currentUser.setEmail("user@test.com");
    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken("user@test.com", null, List.of()));
    when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(currentUser));

    User otherUser = new User();
    otherUser.setId(UUID.randomUUID());
    Store store = new Store();
    store.setUser(otherUser);

    assertThrows(UnauthorizedException.class, () -> authService.assertUserOwnsStore(store));
  }

  // --- logIn ---

  @Test
  void logIn_shouldReturnToken_whenCredentialsAreValid() {
    User user = new User();
    user.setEmail("user@test.com");
    user.setPassword("hashed");
    when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
    when(userService.checkPassword(user, "raw-password")).thenReturn(true);
    when(jwtService.generateToken("user@test.com")).thenReturn("jwt-token");

    assertEquals("jwt-token", authService.logIn("user@test.com", "raw-password"));
  }

  @Test
  void logIn_shouldThrowUnauthorized_whenUserNotFound() {
    when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

    assertThrows(UnauthorizedException.class, () -> authService.logIn("unknown@test.com", "pass"));
  }

  @Test
  void logIn_shouldThrowUnauthorized_whenPasswordIsWrong() {
    User user = new User();
    user.setEmail("user@test.com");
    user.setPassword("hashed");
    when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
    when(userService.checkPassword(user, "wrong")).thenReturn(false);

    assertThrows(UnauthorizedException.class, () -> authService.logIn("user@test.com", "wrong"));
  }

  // --- register ---

  @Test
  void register_shouldCreateUserWithHashedPassword() {
    when(passwordEncoder.encode(anyString())).thenReturn("hashed-password");
    User saved = new User();
    saved.setId(UUID.randomUUID());
    saved.setEmail("test@example.com");
    saved.setPassword("hashed-password");
    when(userRepository.save(any(User.class))).thenReturn(saved);

    User result = authService.register("test@example.com", "raw-password");

    assertNotNull(result.getId());
    assertEquals("test@example.com", result.getEmail());
    assertEquals("hashed-password", result.getPassword());
  }
}
