package ispp.project.dondesiempre.services;

import ispp.project.dondesiempre.controllers.auth.dto.UserResponseDTO;
import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.models.User;
import ispp.project.dondesiempre.models.clients.Client;
import ispp.project.dondesiempre.models.clients.ClientDTO;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.models.stores.dto.StoreDTO;
import ispp.project.dondesiempre.repositories.ClientRepository;
import ispp.project.dondesiempre.repositories.UserRepository;
import ispp.project.dondesiempre.repositories.stores.StoreRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
  private final StoreRepository storeRepository;
  private final ClientRepository clientRepository;

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
  public User logIn(String email, String password) throws UnauthorizedException {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new UnauthorizedException("Invalid credentials."));
    if (!userService.checkPassword(user, password)) {
      throw new UnauthorizedException("Invalid credentials.");
    }
    return user;
  }

  @Transactional(rollbackFor = Exception.class)
  public User register(String email, String password) {
    User user = new User();
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(password));
    return userRepository.save(user);
  }

  @Transactional(readOnly = true)
  public UserResponseDTO buildUserResponse(User user, String rawToken) {
    Optional<Store> storeOpt = storeRepository.findByUserId(user.getId());
    Optional<Client> clientOpt = clientRepository.findByUserId(user.getId());

    List<String> roles = new ArrayList<>();
    if (storeOpt.isPresent()) roles.add("STORE");
    if (clientOpt.isPresent()) roles.add("CLIENT");

    return new UserResponseDTO(
        user.getId(),
        user.getEmail(),
        roles,
        jwtService.getExpiresAt(rawToken),
        storeOpt.map(StoreDTO::new).orElse(null),
        clientOpt.map(ClientDTO::new).orElse(null));
  }
}
