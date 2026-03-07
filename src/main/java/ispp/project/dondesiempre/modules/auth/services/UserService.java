package ispp.project.dondesiempre.modules.auth.services;

import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.clients.models.Client;
import ispp.project.dondesiempre.modules.clients.repositories.ClientRepository;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.common.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.repositories.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  /** Email used for the seed store owner. Referenced by DataSeeder. */
  public static final String SEED_USER_EMAIL = "owner@laboutique.es";

  private final ClientRepository clientRepository;
  private final StoreRepository storeRepository;
  private final PasswordEncoder passwordEncoder;
  private final ApplicationContext applicationContext;

  public boolean checkPassword(User user, String rawPassword) {
    return passwordEncoder.matches(rawPassword, user.getPassword());
  }

  @Transactional(
      readOnly = true,
      rollbackFor = {ResourceNotFoundException.class, UnauthorizedException.class})
  public Client getCurrentClient() throws ResourceNotFoundException, UnauthorizedException {
    User currentUser = applicationContext.getBean(AuthService.class).getCurrentUser();
    return clientRepository
        .findByUserId(currentUser.getId())
        .orElseThrow(() -> new ResourceNotFoundException("Current client not found."));
  }

  @Transactional(
      readOnly = true,
      rollbackFor = {ResourceNotFoundException.class, UnauthorizedException.class})
  public Store getCurrentStore() throws ResourceNotFoundException, UnauthorizedException {
    User currentUser = applicationContext.getBean(AuthService.class).getCurrentUser();
    return storeRepository
        .findByUserId(currentUser.getId())
        .orElseThrow(() -> new ResourceNotFoundException("Current store not found."));
  }
}
