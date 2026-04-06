package ispp.project.dondesiempre.modules.stores.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ispp.project.dondesiempre.mockEntities.StoreMockEntities;
import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.auth.services.AuthService;
import ispp.project.dondesiempre.modules.common.exceptions.InvalidBoundingBoxException;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.common.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.modules.promotions.repositories.PromotionRepository;
import ispp.project.dondesiempre.modules.stores.dtos.StoreDTO;
import ispp.project.dondesiempre.modules.stores.dtos.StoreUpdateDTO;
import ispp.project.dondesiempre.modules.stores.models.SocialNetwork;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.models.StoreSocialNetwork;
import ispp.project.dondesiempre.modules.stores.models.Storefront;
import ispp.project.dondesiempre.modules.stores.repositories.StoreRepository;
import ispp.project.dondesiempre.modules.stores.repositories.StoreSocialNetworkRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

@ExtendWith(MockitoExtension.class)
public class StoreServiceTest {

  @Mock private StoreRepository storeRepository;
  @Mock private StoreSocialNetworkRepository socialNetworkRepository;
  @Mock private PromotionRepository promotionRepository;
  @Mock private AuthService authService;

  @Mock private ApplicationContext applicationContext;

  @InjectMocks private StoreService storeService;

  private Store store;
  private UUID storeId;

  private User owner;
  private User intruder;

  @BeforeEach
  void setUp() {
    store = new Store();
    storeId = UUID.randomUUID();
    store.setId(storeId);
    store.setName("Tienda de Prueba");
    Storefront storefront = new Storefront();

    store.setStorefront(storefront);

    owner = new User();
    owner.setEmail("owner@test.com");
    store.setUser(owner);

    intruder = new User();
    intruder.setEmail("intruder@test.com");
  }

  private static final UUID TEST_STORE_ID = UUID.randomUUID();
  private static final Store TEST_STORE = StoreMockEntities.sampleStore(TEST_STORE_ID);

  @Test
  void shouldReturnStore_whenFindByIdExists() {
    when(storeRepository.findById(TEST_STORE_ID)).thenReturn(Optional.of(TEST_STORE));

    Store result = storeService.findById(TEST_STORE_ID);

    assertEquals("Tienda de Prueba", result.getName());
    verify(storeRepository, times(1)).findById(TEST_STORE_ID);
  }

  @Test
  void shouldThrowResourceNotFoundException_whenFindByIdDoesNotExist() {
    UUID nonExistentId = UUID.randomUUID();
    when(storeRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> storeService.findById(nonExistentId));

    verify(storeRepository, times(1)).findById(nonExistentId);
  }

  @Test
  void shouldReturnStoreList_whenBoundingBoxIsValid() {
    double minLon = -6.0, minLat = 37.0, maxLon = -5.0, maxLat = 38.0;
    when(storeRepository.findStoresInBoundingBox(minLon, minLat, maxLon, maxLat, 500))
        .thenReturn(List.of(TEST_STORE));

    List<Store> result = storeService.findStoresInBoundingBox(minLon, minLat, maxLon, maxLat);

    assertEquals(1, result.size());
    verify(storeRepository, times(1)).findStoresInBoundingBox(minLon, minLat, maxLon, maxLat, 500);
  }

  @Test
  void shouldThrowInvalidBoundingBoxException_whenMinLonIsGreaterThanMaxLon() {
    assertThrows(
        InvalidBoundingBoxException.class,
        () -> storeService.findStoresInBoundingBox(-5.0, 37.0, -6.0, 38.0));

    verify(storeRepository, times(0))
        .findStoresInBoundingBox(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyInt());
  }

  @Test
  void shouldThrowInvalidBoundingBoxException_whenMinLatIsGreaterThanMaxLat() {
    assertThrows(
        InvalidBoundingBoxException.class,
        () -> storeService.findStoresInBoundingBox(-6.0, 38.0, -5.0, 37.0));

    verify(storeRepository, times(0))
        .findStoresInBoundingBox(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyInt());
  }

  @Test
  void shouldReturnStoreDTOWithSocialNetworks_whenToDTOCalled() {
    SocialNetwork sn = new SocialNetwork();
    sn.setName("instagram");
    StoreSocialNetwork ssn = new StoreSocialNetwork();
    ssn.setLink("https://instagram.com/store");
    ssn.setSocialNetwork(sn);

    when(socialNetworkRepository.findByStoreId(TEST_STORE_ID)).thenReturn(List.of(ssn));
    when(promotionRepository.existsByStoreIdAndIsActiveTrue(TEST_STORE_ID)).thenReturn(true);

    StoreDTO result = storeService.toDTO(TEST_STORE);

    assertEquals("Tienda de Prueba", result.getName());
    assertEquals(1, result.getSocialNetworks().size());
    assertEquals("instagram", result.getSocialNetworks().get(0).getName());
    assertEquals("https://instagram.com/store", result.getSocialNetworks().get(0).getLink());
  }

  @Test
  void shouldReturnStoreDTO_whenUpdateStoreIsSuccessful()
      throws UnauthorizedException, ResourceNotFoundException {

    when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
    when(storeRepository.save(store)).thenReturn(store);
    when(applicationContext.getBean(StoreService.class)).thenReturn(storeService);

    StoreUpdateDTO storeUpdateDTO = new StoreUpdateDTO();
    storeUpdateDTO.setName("Nombre Actualizado");

    StoreDTO result = storeService.updateStore(storeId, storeUpdateDTO);

    assertEquals("Nombre Actualizado", result.getName());
    assertEquals("Nombre Actualizado", store.getName());

    verify(storeRepository, times(1)).findById(storeId);
    verify(authService, times(1)).assertUserOwnsStore(store);
    verify(storeRepository, times(1)).save(store);
  }

  @Test
  void shouldUpdateOnlyNonNullFieldsSuccessfully()
      throws UnauthorizedException, ResourceNotFoundException {

    when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
    when(storeRepository.save(store)).thenReturn(store);
    when(applicationContext.getBean(StoreService.class)).thenReturn(storeService);

    StoreUpdateDTO dto = new StoreUpdateDTO();
    dto.setName("Nombre Nuevo");
    dto.setPhone("123456789");

    StoreDTO result = storeService.updateStore(storeId, dto);

    assertEquals("Nombre Nuevo", store.getName());
    assertEquals("123456789", store.getPhone().orElse(null));
    assertEquals("Nombre Nuevo", result.getName());
    assertEquals("123456789", result.getPhone());

    verify(storeRepository, times(1)).findById(storeId);
    verify(authService, times(1)).assertUserOwnsStore(store);
    verify(storeRepository, times(1)).save(store);
  }

  @Test
  void shouldThrowResourceNotFound_whenStoreDoesNotExist() {
    UUID nonExistentId = UUID.randomUUID();
    when(storeRepository.findById(nonExistentId)).thenReturn(Optional.empty());
    when(applicationContext.getBean(StoreService.class)).thenReturn(storeService);

    StoreUpdateDTO dto = new StoreUpdateDTO();

    assertThrows(
        ResourceNotFoundException.class, () -> storeService.updateStore(nonExistentId, dto));
    verify(storeRepository, never()).save(any());
  }

  @Test
  void shouldThrowUnauthorized_whenUserIsNotOwner() throws ResourceNotFoundException {
    when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
    doThrow(new UnauthorizedException("Not owner")).when(authService).assertUserOwnsStore(store);
    when(applicationContext.getBean(StoreService.class)).thenReturn(storeService);

    StoreUpdateDTO dto = new StoreUpdateDTO();

    assertThrows(UnauthorizedException.class, () -> storeService.updateStore(storeId, dto));
    verify(storeRepository, never()).save(any());
  }

  @Test
  void shouldUpdateOnlySpecifiedFields() throws UnauthorizedException, ResourceNotFoundException {

    when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
    when(storeRepository.save(store)).thenReturn(store);
    when(applicationContext.getBean(StoreService.class)).thenReturn(storeService);

    StoreUpdateDTO dto = new StoreUpdateDTO();
    dto.setEmail("nuevo@email.com");
    dto.setPhone("555555");

    StoreDTO result = storeService.updateStore(storeId, dto);

    assertEquals("nuevo@email.com", store.getEmail());
    assertEquals("555555", store.getPhone().orElse(null));
    assertEquals("Tienda de Prueba", store.getName());

    assertEquals("nuevo@email.com", result.getEmail());
    assertEquals("555555", result.getPhone());
    assertEquals("Tienda de Prueba", result.getName());

    verify(storeRepository, times(1)).findById(storeId);
    verify(authService, times(1)).assertUserOwnsStore(store);
    verify(storeRepository, times(1)).save(store);
  }

  @Test
  void shouldUpdateLocationSuccessfully_whenUserIsOwner()
      throws UnauthorizedException, ResourceNotFoundException {

    when(applicationContext.getBean(StoreService.class)).thenReturn(storeService);
    when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
    when(storeRepository.save(store)).thenReturn(store);

    when(socialNetworkRepository.findByStoreId(storeId)).thenReturn(List.of());
    when(promotionRepository.existsByStoreIdAndIsActiveTrue(storeId)).thenReturn(false);

    Double newLon = -5.9281;
    Double newLat = 37.2829;

    StoreDTO result = storeService.updateLocation(storeId, newLon, newLat);

    Point location = store.getLocation();

    assertEquals(newLon, location.getX());
    assertEquals(newLat, location.getY());
    assertEquals(4326, location.getSRID());

    assertEquals("Tienda de Prueba", result.getName());

    verify(storeRepository, times(1)).findById(storeId);
    verify(authService, times(1)).assertUserOwnsStore(store);
    verify(storeRepository, times(1)).save(store);
  }

  @Test
  void shouldThrowUnauthorized_whenUpdatingLocationAndNotOwner() throws ResourceNotFoundException {
    when(applicationContext.getBean(StoreService.class)).thenReturn(storeService);
    when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
    doThrow(new UnauthorizedException("You do not own this store."))
        .when(authService)
        .assertUserOwnsStore(store);

    assertThrows(
        UnauthorizedException.class, () -> storeService.updateLocation(storeId, -5.9281, 37.2829));

    verify(storeRepository, never()).save(any());
  }

  @Test
  void shouldThrowResourceNotFound_whenUpdatingLocationOfNonExistentStore() {
    UUID nonExistentId = UUID.randomUUID();
    when(applicationContext.getBean(StoreService.class)).thenReturn(storeService);
    when(storeRepository.findById(nonExistentId)).thenReturn(Optional.empty());
    assertThrows(
        ResourceNotFoundException.class,
        () -> storeService.updateLocation(nonExistentId, -5.9281, 37.2829));

    verify(storeRepository, never()).save(any());
  }
}
