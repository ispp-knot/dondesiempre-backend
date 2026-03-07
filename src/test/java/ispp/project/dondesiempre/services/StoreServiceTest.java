package ispp.project.dondesiempre.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ispp.project.dondesiempre.exceptions.InvalidBoundingBoxException;
import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.mockEntities.StoreMockEntities;
import ispp.project.dondesiempre.models.storefronts.Storefront;
import ispp.project.dondesiempre.modules.auth.services.UserService;
import ispp.project.dondesiempre.modules.clients.models.Client;
import ispp.project.dondesiempre.modules.follows.models.StoreFollower;
import ispp.project.dondesiempre.modules.follows.repositories.StoreFollowerRepository;
import ispp.project.dondesiempre.modules.stores.dtos.StoreDTO;
import ispp.project.dondesiempre.modules.stores.models.SocialNetwork;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.models.StoreSocialNetwork;
import ispp.project.dondesiempre.modules.stores.repositories.StoreRepository;
import ispp.project.dondesiempre.modules.stores.repositories.StoreSocialNetworkRepository;
import ispp.project.dondesiempre.modules.stores.services.StoreService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

@ExtendWith(MockitoExtension.class)
public class StoreServiceTest {

  @Mock
  private StoreRepository storeRepository;
  @Mock
  private StoreSocialNetworkRepository socialNetworkRepository;
  @Mock
  private StoreFollowerRepository storeFollowerRepository;
  @Mock
  private UserService userService;

  @Mock
  private ApplicationContext applicationContext;

  @InjectMocks
  private StoreService storeService;

  private Store store;
  private UUID storeId;

  @BeforeEach
  void setUp() {
    store = new Store();
    storeId = UUID.randomUUID();
    store.setId(storeId);
    store.setName("Tienda de Prueba");
    Storefront storefront = new Storefront();
    storefront.setIsFirstCollections(true);
    store.setStorefront(storefront);
  }

  private static final Client TEST_CLIENT = StoreMockEntities.sampleClient();
  private static final UUID TEST_STORE_ID = UUID.randomUUID();
  private static final Store TEST_STORE = StoreMockEntities.sampleStore(TEST_STORE_ID);
  private static final StoreFollower TEST_FOLLOWER = StoreMockEntities.sampleFollower(TEST_CLIENT, TEST_STORE);

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

    StoreDTO result = storeService.toDTO(TEST_STORE);

    assertEquals("Tienda de Prueba", result.getName());
    assertEquals(1, result.getSocialNetworks().size());
    assertEquals("instagram", result.getSocialNetworks().get(0).getName());
    assertEquals("https://instagram.com/store", result.getSocialNetworks().get(0).getLink());
  }

  @Test
  void followStore_shouldFollowStore_whenStoreExists() {
    when(userService.getCurrentClient()).thenReturn(TEST_CLIENT);
    when(storeRepository.findById(TEST_STORE_ID)).thenReturn(Optional.of(TEST_STORE));
    when(storeFollowerRepository.save(any())).thenReturn(TEST_FOLLOWER);

    StoreFollower follow = storeService.followStore(TEST_STORE_ID);

    assertEquals(follow.getClient().getId(), TEST_FOLLOWER.getClient().getId());
    assertEquals(follow.getStore().getId(), TEST_FOLLOWER.getStore().getId());
    verify(storeFollowerRepository).save(follow);
  }

  @Test
  void unfollowStore_shouldThrowNotFound_whenNotPreviouslyFollowed() {
    when(userService.getCurrentClient()).thenReturn(TEST_CLIENT);
    when(storeFollowerRepository.findByClientIdAndStoreId(TEST_CLIENT.getId(), TEST_STORE_ID))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> storeService.unfollowStore(TEST_STORE_ID));
    verify(storeFollowerRepository, never()).delete(any());
  }

  @Test
  void unfollowStore_shouldUnfollowStore_whenPreviouslyFollowed() {
    when(userService.getCurrentClient()).thenReturn(TEST_CLIENT);
    when(storeFollowerRepository.findByClientIdAndStoreId(TEST_CLIENT.getId(), TEST_STORE_ID))
        .thenReturn(Optional.of(TEST_FOLLOWER));
    doNothing().when(storeFollowerRepository).delete(any());

    storeService.unfollowStore(TEST_STORE_ID);

    verify(storeFollowerRepository, times(1)).delete(TEST_FOLLOWER);
  }

  @Test
  void getMyFollowedStores_shouldGetZeroStores_whenNotFollowingAnyStore() {
    when(userService.getCurrentClient()).thenReturn(TEST_CLIENT);
    when(storeFollowerRepository.findByClientId(TEST_CLIENT.getId())).thenReturn(List.of());

    List<Store> stores = storeService.getMyFollowedStores();

    assertEquals(stores.size(), 0);
  }

  @Test
  void getMyFollowedStores_shouldGetOneStore_whenFollowingOneStore() {
    when(userService.getCurrentClient()).thenReturn(TEST_CLIENT);
    when(storeFollowerRepository.findByClientId(TEST_CLIENT.getId()))
        .thenReturn(List.of(TEST_FOLLOWER));

    List<Store> stores = storeService.getMyFollowedStores();

    assertEquals(stores.size(), 1);
    assertEquals(stores.get(0).getId(), new StoreDTO(TEST_STORE).getId());
  }

  @Test
  void getMyFollowedStores_shouldGetMultipleStores_whenFollowingMultipleStores() {
    when(userService.getCurrentClient()).thenReturn(TEST_CLIENT);
    when(storeFollowerRepository.findByClientId(TEST_CLIENT.getId()))
        .thenReturn(List.of(TEST_FOLLOWER, TEST_FOLLOWER, TEST_FOLLOWER));

    List<Store> stores = storeService.getMyFollowedStores();

    assertEquals(stores.size(), 3);
    assertEquals(stores.get(0).getId(), TEST_STORE.getId());
  }

  @Test
  void checkIfIFollowStore_shouldReturnTrue_whenFollowingStore() {
    when(storeFollowerRepository.existsByClientIdAndStoreId(TEST_CLIENT.getId(), TEST_STORE_ID))
        .thenReturn(true);

    boolean res = storeService.checkIfClientFollowsStore(TEST_CLIENT.getId(), TEST_STORE_ID);

    verify(storeFollowerRepository, times(1))
        .existsByClientIdAndStoreId(TEST_CLIENT.getId(), TEST_STORE_ID);
    assertEquals(res, true);
  }

  @Test
  void checkIfIFollowStore_shouldReturnFalse_whenNotFollowingStore() {
    when(storeFollowerRepository.existsByClientIdAndStoreId(TEST_CLIENT.getId(), TEST_STORE_ID))
        .thenReturn(false);

    boolean res = storeService.checkIfClientFollowsStore(TEST_CLIENT.getId(), TEST_STORE_ID);

    verify(storeFollowerRepository, times(1))
        .existsByClientIdAndStoreId(TEST_CLIENT.getId(), TEST_STORE_ID);
    assertEquals(res, false);
  }
}
