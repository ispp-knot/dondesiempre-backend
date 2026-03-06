package ispp.project.dondesiempre.services;

import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.models.Client;
import ispp.project.dondesiempre.models.User;
import ispp.project.dondesiempre.repositories.ClientRepository;
import ispp.project.dondesiempre.repositories.UserRepository;
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

  private final UserRepository userRepository;
  private final ClientRepository clientRepository;
  private final PasswordEncoder passwordEncoder;
  private final ApplicationContext applicationContext;

  @Transactional
  public User register(String email, String password) {
    User user = new User();
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(password));
    return userRepository.save(user);
  }

  public boolean checkPassword(User user, String rawPassword) {
    return passwordEncoder.matches(rawPassword, user.getPassword());
  }

  @Transactional(readOnly = true, rollbackFor = ResourceNotFoundException.class)
  public Client getCurrentClient() throws ResourceNotFoundException {
    User currentUser = applicationContext.getBean(AuthService.class).getCurrentUser();
    return clientRepository
        .findByUserId(currentUser.getId())
        .orElseThrow(() -> new ResourceNotFoundException("Current client not found."));
  }
}
