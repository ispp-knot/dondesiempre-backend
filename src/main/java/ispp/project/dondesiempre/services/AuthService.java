package ispp.project.dondesiempre.services;

import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.models.User;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final UserService userService;
  private final JwtService jwtService;

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
    User currentUser = getCurrentUser();
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
}
