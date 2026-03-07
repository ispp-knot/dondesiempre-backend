package ispp.project.dondesiempre.modules.auth.services;

import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.auth.repositories.UserRepository;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final UserService userService;
  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;
  private final ApplicationContext applicationContext;

  @Transactional(
      readOnly = true,
      rollbackFor = {UnauthorizedException.class, ResourceNotFoundException.class})
  public User getCurrentUser() throws UnauthorizedException, ResourceNotFoundException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null
        || !authentication.isAuthenticated()
        || authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthorizedException("Not authenticated.");
    }
    String email = authentication.getName();
    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found."));
  }

  @Transactional(readOnly = true, rollbackFor = UnauthorizedException.class)
  public void assertUserOwnsStore(Store store) throws UnauthorizedException {
    User currentUser = applicationContext.getBean(AuthService.class).getCurrentUser();
    if (!store.getUser().equals(currentUser)) {
      throw new UnauthorizedException("You do not own this store.");
    }
  }

  @Transactional(readOnly = true, rollbackFor = UnauthorizedException.class)
  public String logIn(String email, String password) throws UnauthorizedException {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new UnauthorizedException("Invalid credentials."));
    if (!userService.checkPassword(user, password)) {
      throw new UnauthorizedException("Invalid credentials.");
    }
    return jwtService.generateToken(user.getEmail());
  }

  @Transactional(rollbackFor = Exception.class)
  public User register(String email, String password) {
    User user = new User();
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(password));
    return userRepository.save(user);
  }
}
