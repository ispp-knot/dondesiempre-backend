package ispp.project.dondesiempre.modules.follows.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ispp.project.dondesiempre.mockEntities.StoreMockEntities;
import ispp.project.dondesiempre.modules.auth.services.UserService;
import ispp.project.dondesiempre.modules.clients.models.Client;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.follows.models.StoreFollower;
import ispp.project.dondesiempre.modules.follows.repositories.StoreFollowerRepository;
import ispp.project.dondesiempre.modules.stores.dtos.StoreDTO;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.models.Storefront;
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

@ExtendWith(MockitoExtension.class)
public class StoreFollowerServiceTest {

  @Mock private StoreFollowerRepository storeFollowerRepository;
  @Mock private UserService userService;
  @Mock private StoreService storeService;

  @InjectMocks private StoreFollowerService storeFollowerService;

  private Store store;
  private UUID storeId;

  @BeforeEach
  void setUp() {
    store = new Store();
    storeId = UUID.randomUUID();
    store.setId(storeId);
    store.setName("Tienda de Prueba");
    Storefront storefront = new Storefront();
    store.setStorefront(storefront);
  }

  private static final Client TEST_CLIENT = StoreMockEntities.sampleClient();
  private static final UUID TEST_STORE_ID = UUID.randomUUID();
  private static final Store TEST_STORE = StoreMockEntities.sampleStore(TEST_STORE_ID);
  private static final StoreFollower TEST_FOLLOWER =
      StoreMockEntities.sampleFollower(TEST_CLIENT, TEST_STORE);

  @Test
  void followStore_shouldFollowStore_whenStoreExists() {
    when(userService.getCurrentClient()).thenReturn(TEST_CLIENT);
    when(storeFollowerRepository.save(any())).thenReturn(TEST_FOLLOWER);
    when(storeService.findById(TEST_STORE_ID)).thenReturn(TEST_FOLLOWER.getStore());

    StoreFollower follow = storeFollowerService.followStore(TEST_STORE_ID);

    assertEquals(follow.getClient().getId(), TEST_FOLLOWER.getClient().getId());
    assertEquals(follow.getStore().getId(), TEST_FOLLOWER.getStore().getId());
    verify(storeFollowerRepository).save(follow);
    verify(storeService).findById(TEST_STORE_ID);
  }

  @Test
  void unfollowStore_shouldThrowNotFound_whenNotPreviouslyFollowed() {
    when(userService.getCurrentClient()).thenReturn(TEST_CLIENT);
    when(storeFollowerRepository.findByClientIdAndStoreId(TEST_CLIENT.getId(), TEST_STORE_ID))
        .thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> storeFollowerService.unfollowStore(TEST_STORE_ID));
    verify(storeFollowerRepository, never()).delete(any());
  }

  @Test
  void unfollowStore_shouldUnfollowStore_whenPreviouslyFollowed() {
    when(userService.getCurrentClient()).thenReturn(TEST_CLIENT);
    when(storeFollowerRepository.findByClientIdAndStoreId(TEST_CLIENT.getId(), TEST_STORE_ID))
        .thenReturn(Optional.of(TEST_FOLLOWER));
    doNothing().when(storeFollowerRepository).delete(any());

    storeFollowerService.unfollowStore(TEST_STORE_ID);

    verify(storeFollowerRepository, times(1)).delete(TEST_FOLLOWER);
  }

  @Test
  void getMyFollowedStores_shouldGetZeroStores_whenNotFollowingAnyStore() {
    when(userService.getCurrentClient()).thenReturn(TEST_CLIENT);
    when(storeFollowerRepository.findStoresByClientId(TEST_CLIENT.getId())).thenReturn(List.of());

    List<Store> stores = storeFollowerService.getMyFollowedStores();

    assertEquals(stores.size(), 0);
  }

  @Test
  void getMyFollowedStores_shouldGetOneStore_whenFollowingOneStore() {
    when(userService.getCurrentClient()).thenReturn(TEST_CLIENT);
    when(storeFollowerRepository.findStoresByClientId(TEST_CLIENT.getId()))
        .thenReturn(List.of(TEST_FOLLOWER.getStore()));

    List<Store> stores = storeFollowerService.getMyFollowedStores();

    assertEquals(stores.size(), 1);
    assertEquals(stores.get(0).getId(), new StoreDTO(TEST_STORE).getId());
  }

  @Test
  void getMyFollowedStores_shouldGetMultipleStores_whenFollowingMultipleStores() {
    when(userService.getCurrentClient()).thenReturn(TEST_CLIENT);
    when(storeFollowerRepository.findStoresByClientId(TEST_CLIENT.getId()))
        .thenReturn(
            List.of(TEST_FOLLOWER.getStore(), TEST_FOLLOWER.getStore(), TEST_FOLLOWER.getStore()));

    List<Store> stores = storeFollowerService.getMyFollowedStores();

    assertEquals(stores.size(), 3);
    assertEquals(stores.get(0).getId(), TEST_STORE.getId());
  }

  @Test
  void checkIfIFollowStore_shouldReturnTrue_whenFollowingStore() {
    when(storeFollowerRepository.existsByClientIdAndStoreId(TEST_CLIENT.getId(), TEST_STORE_ID))
        .thenReturn(true);

    boolean res =
        storeFollowerService.checkIfClientFollowsStore(TEST_CLIENT.getId(), TEST_STORE_ID);

    verify(storeFollowerRepository, times(1))
        .existsByClientIdAndStoreId(TEST_CLIENT.getId(), TEST_STORE_ID);
    assertEquals(res, true);
  }

  @Test
  void checkIfIFollowStore_shouldReturnFalse_whenNotFollowingStore() {
    when(storeFollowerRepository.existsByClientIdAndStoreId(TEST_CLIENT.getId(), TEST_STORE_ID))
        .thenReturn(false);

    boolean res =
        storeFollowerService.checkIfClientFollowsStore(TEST_CLIENT.getId(), TEST_STORE_ID);

    verify(storeFollowerRepository, times(1))
        .existsByClientIdAndStoreId(TEST_CLIENT.getId(), TEST_STORE_ID);
    assertEquals(res, false);
  }
}
