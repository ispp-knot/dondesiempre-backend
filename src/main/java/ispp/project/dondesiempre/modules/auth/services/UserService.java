package ispp.project.dondesiempre.modules.auth.services;

import ispp.project.dondesiempre.modules.auth.dtos.RegisterClientDTO;
import ispp.project.dondesiempre.modules.auth.dtos.RegisterStoreDTO;
import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.auth.repositories.UserRepository;
import ispp.project.dondesiempre.modules.clients.dtos.ClientDTO;
import ispp.project.dondesiempre.modules.clients.models.Client;
import ispp.project.dondesiempre.modules.clients.repositories.ClientRepository;
import ispp.project.dondesiempre.modules.common.exceptions.AlreadyExistsException;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.common.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.models.Storefront;
import ispp.project.dondesiempre.modules.stores.repositories.StoreRepository;
import ispp.project.dondesiempre.modules.stores.repositories.StorefrontRepository;
import ispp.project.dondesiempre.utils.cloudinary.CoordinatesService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final ClientRepository clientRepository;
  private final StoreRepository storeRepository;
  private final StorefrontRepository storefrontRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final ApplicationContext applicationContext;
  private final CoordinatesService coordinatesService;

  public boolean checkPassword(User user, String rawPassword) {
    return passwordEncoder.matches(rawPassword, user.getPassword());
  }

  @Transactional(readOnly = true)
  public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  @Transactional
  public User save(User user) {
    return userRepository.save(user);
  }

  @Transactional(rollbackFor = Exception.class)
  public Store registerStore(RegisterStoreDTO dto) {
    if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
      throw new AlreadyExistsException("Email already in use.");
    }

    User user = new User();
    user.setEmail(dto.getEmail());
    user.setPassword(passwordEncoder.encode(dto.getPassword()));
    userRepository.save(user);

    Storefront storefront = new Storefront();
    if (dto.getPrimaryColor() != null) storefront.setPrimaryColor(dto.getPrimaryColor());
    if (dto.getSecondaryColor() != null) storefront.setSecondaryColor(dto.getSecondaryColor());
    storefrontRepository.save(storefront);

    Store store = new Store();
    store.setName(dto.getName());
    store.setEmail(dto.getEmail());
    store.setLocation(coordinatesService.createPoint(dto.getLongitude(), dto.getLatitude()));
    store.setAddress(dto.getAddress());
    store.setOpeningHours(dto.getOpeningHours());
    store.setAcceptsShipping(dto.getAcceptsShipping());
    store.setPhone(dto.getPhone());
    store.setAboutUs(dto.getAboutUs());
    store.setStorefront(storefront);
    store.setUser(user);

    return storeRepository.save(store);
  }

  @Transactional(rollbackFor = Exception.class)
  public ClientDTO registerClient(RegisterClientDTO dto) {
    if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
      throw new AlreadyExistsException("Email already in use.");
    }

    User user = new User();
    user.setEmail(dto.getEmail());
    user.setPassword(passwordEncoder.encode(dto.getPassword()));
    userRepository.save(user);

    Client client = new Client();
    client.setName(dto.getName());
    client.setSurname(dto.getSurname());
    client.setEmail(dto.getEmail());
    client.setPhone(dto.getPhone());
    client.setAddress(dto.getAddress());
    client.setUser(user);

    return new ClientDTO(clientRepository.save(client));
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

  @Transactional
  public void changePassword(String oldPassword, String newPassword) {
    User currentUser = applicationContext.getBean(AuthService.class).getCurrentUser();

    if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
      throw new UnauthorizedException("Wrong password.");
    }

    currentUser.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(currentUser);
  }
}
