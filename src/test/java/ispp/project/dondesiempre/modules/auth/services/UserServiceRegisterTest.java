package ispp.project.dondesiempre.modules.auth.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ispp.project.dondesiempre.modules.auth.dtos.RegisterClientDTO;
import ispp.project.dondesiempre.modules.auth.dtos.RegisterStoreDTO;
import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.auth.repositories.UserRepository;
import ispp.project.dondesiempre.modules.clients.dtos.ClientDTO;
import ispp.project.dondesiempre.modules.clients.models.Client;
import ispp.project.dondesiempre.modules.clients.repositories.ClientRepository;
import ispp.project.dondesiempre.modules.common.exceptions.AlreadyExistsException;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.models.Storefront;
import ispp.project.dondesiempre.modules.stores.repositories.StoreRepository;
import ispp.project.dondesiempre.modules.stores.repositories.StorefrontRepository;
import ispp.project.dondesiempre.utils.cloudinary.CoordinatesService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceRegisterTest {

  @Mock private UserRepository userRepository;
  @Mock private ClientRepository clientRepository;
  @Mock private StoreRepository storeRepository;
  @Mock private StorefrontRepository storefrontRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private ApplicationContext applicationContext;
  @Mock private CoordinatesService coordinatesService;

  @InjectMocks private UserService userService;

  // --- registerStore ---

  @Test
  void registerStore_shouldCreateUserStorefrontAndStore() {
    when(userRepository.findByEmail("store@test.com")).thenReturn(Optional.empty());
    when(passwordEncoder.encode("Password1!")).thenReturn("hashed");
    when(userRepository.save(any(User.class))).thenReturn(new User());
    when(storefrontRepository.save(any(Storefront.class))).thenReturn(new Storefront());
    when(coordinatesService.createPoint(anyDouble(), anyDouble()))
        .thenAnswer(
            invocation -> {
              double lon = invocation.getArgument(0);
              double lat = invocation.getArgument(1);
              return new GeometryFactory(new PrecisionModel(), 4326)
                  .createPoint(new Coordinate(lon, lat));
            });
    Store savedStore = new Store();
    savedStore.setId(UUID.randomUUID());
    savedStore.setName("Test Store");
    when(storeRepository.save(any(Store.class))).thenReturn(savedStore);

    Store result = userService.registerStore(buildStoreDTO("store@test.com"));

    assertNotNull(result.getId());
    assertEquals("Test Store", result.getName());
    verify(storefrontRepository).save(any(Storefront.class));
    verify(storeRepository).save(any(Store.class));
  }

  @Test
  void registerStore_shouldApplyCustomColors_whenProvided() {
    when(userRepository.findByEmail("store@test.com")).thenReturn(Optional.empty());
    when(passwordEncoder.encode(anyString())).thenReturn("hashed");
    when(userRepository.save(any(User.class))).thenReturn(new User());
    when(storefrontRepository.save(any(Storefront.class))).thenAnswer(inv -> inv.getArgument(0));

    Store savedStore = new Store();
    savedStore.setId(UUID.randomUUID());
    when(storeRepository.save(any(Store.class))).thenReturn(savedStore);

    RegisterStoreDTO dto = buildStoreDTO("store@test.com");
    dto.setPrimaryColor("#000000");
    dto.setSecondaryColor("#ffffff");

    userService.registerStore(dto);

    verify(storefrontRepository)
        .save(
            org.mockito.ArgumentMatchers.argThat(
                sf ->
                    "#000000".equals(sf.getPrimaryColor())
                        && "#ffffff".equals(sf.getSecondaryColor())));
  }

  @Test
  void registerStore_shouldThrowAlreadyExistsException_whenEmailTaken() {
    User existing = new User();
    existing.setEmail("taken@test.com");
    when(userRepository.findByEmail("taken@test.com")).thenReturn(Optional.of(existing));

    assertThrows(
        AlreadyExistsException.class,
        () -> userService.registerStore(buildStoreDTO("taken@test.com")));
  }

  // --- registerClient ---

  @Test
  void registerClient_shouldCreateUserAndClient() {
    when(userRepository.findByEmail("client@test.com")).thenReturn(Optional.empty());
    when(passwordEncoder.encode("Password1!")).thenReturn("hashed");
    when(userRepository.save(any(User.class))).thenReturn(new User());

    Client savedClient = new Client();
    savedClient.setId(UUID.randomUUID());
    savedClient.setName("John");
    savedClient.setSurname("Doe");
    savedClient.setEmail("client@test.com");
    savedClient.setAddress("Test Street 1");
    when(clientRepository.save(any(Client.class))).thenReturn(savedClient);

    ClientDTO result = userService.registerClient(buildClientDTO("client@test.com"));

    assertNotNull(result.getId());
    assertEquals("John", result.getName());
    assertEquals("Doe", result.getSurname());
    verify(clientRepository).save(any(Client.class));
  }

  @Test
  void registerClient_shouldThrowAlreadyExistsException_whenEmailTaken() {
    User existing = new User();
    existing.setEmail("taken@test.com");
    when(userRepository.findByEmail("taken@test.com")).thenReturn(Optional.of(existing));

    assertThrows(
        AlreadyExistsException.class,
        () -> userService.registerClient(buildClientDTO("taken@test.com")));
  }

  // --- helpers ---

  private RegisterStoreDTO buildStoreDTO(String email) {
    RegisterStoreDTO dto = new RegisterStoreDTO();
    dto.setEmail(email);
    dto.setPassword("Password1!");
    dto.setName("Test Store");
    dto.setLatitude(40.416775);
    dto.setLongitude(-3.703790);
    dto.setAddress("Gran Vía 1, Madrid");
    dto.setOpeningHours("Mon-Fri 9-18");
    dto.setAboutUs("A great store.");
    dto.setPrimaryColor("#c65a3a");
    dto.setSecondaryColor("#19756a");
    return dto;
  }

  private RegisterClientDTO buildClientDTO(String email) {
    RegisterClientDTO dto = new RegisterClientDTO();
    dto.setEmail(email);
    dto.setPassword("Password1!");
    dto.setName("John");
    dto.setSurname("Doe");
    dto.setAddress("Test Street 1");
    return dto;
  }
}
