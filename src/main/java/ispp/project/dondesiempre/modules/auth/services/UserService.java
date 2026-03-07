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
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
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

  public boolean checkPassword(User user, String rawPassword) {
    return passwordEncoder.matches(rawPassword, user.getPassword());
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

    GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);

    Store store = new Store();
    store.setName(dto.getName());
    store.setEmail(dto.getEmail());
    store.setStoreID(dto.getStoreID());
    store.setLocation(gf.createPoint(new Coordinate(dto.getLongitude(), dto.getLatitude())));
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
}
