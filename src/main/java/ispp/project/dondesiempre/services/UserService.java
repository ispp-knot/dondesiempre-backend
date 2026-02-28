package ispp.project.dondesiempre.services;

import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.models.Client;
import ispp.project.dondesiempre.models.User;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.repositories.ClientRepository;
import ispp.project.dondesiempre.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  public static final String SEED_USER_EMAIL = "owner@laboutique.es";

  private final UserRepository userRepository;
  private final ClientRepository clientRepository;

  @Autowired @Lazy private UserService userService;

  @Transactional(readOnly = true)
  public User getCurrentUser() {
    return userRepository
        .findByEmail(SEED_USER_EMAIL)
        .orElseThrow(
            () -> new ResourceNotFoundException("Current user not found. Is the database seeded?"));
  }

  @Transactional(readOnly = true)
  public Client getCurrentClient() {
    User currentUser = getCurrentUser();
    return clientRepository
        .findByUserId(currentUser.getId())
        .orElseThrow(
            () ->
                new ResourceNotFoundException("Current client not found. Is the database seeded?"));
  }

  @Transactional(readOnly = true)
  public void assertUserOwnsStore(Store store) {
    User currentUser = userService.getCurrentUser();
    if (!store.getUser().equals(currentUser)) {
      throw new UnauthorizedException("You do not own this store.");
    }
  }
}
