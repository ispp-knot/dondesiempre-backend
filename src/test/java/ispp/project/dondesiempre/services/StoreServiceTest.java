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
import ispp.project.dondesiempre.models.Client;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.models.stores.StoreFollower;
import ispp.project.dondesiempre.models.stores.dto.StoreDTO;
import ispp.project.dondesiempre.repositories.stores.StoreFollowerRepository;
import ispp.project.dondesiempre.repositories.stores.StoreRepository;
import ispp.project.dondesiempre.services.stores.StoreService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StoreServiceTest {

  @Mock private StoreRepository storeRepository;
  @Mock private StoreFollowerRepository storeFollowerRepository;

  @Mock private UserService userService;

  @InjectMocks private StoreService storeService;

  private static final Client TEST_CLIENT = StoreMockEntities.sampleClient();
  private static final UUID TEST_STORE_ID = UUID.randomUUID();
  private static final Store TEST_STORE = StoreMockEntities.sampleStore(TEST_STORE_ID);
  private static final StoreFollower TEST_FOLLOWER =
      StoreMockEntities.sampleFollower(TEST_CLIENT, TEST_STORE);

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
    // Dado
    double minLon = -6.0, minLat = 37.0, maxLon = -5.0, maxLat = 38.0;
    when(storeRepository.findStoresInBoundingBox(minLon, minLat, maxLon, maxLat, 500))
        .thenReturn(List.of(TEST_STORE));

    // Cuando
    List<StoreDTO> result = storeService.findStoresInBoundingBox(minLon, minLat, maxLon, maxLat);

    // Entonces
    assertEquals(1, result.size());
    verify(storeRepository, times(1)).findStoresInBoundingBox(minLon, minLat, maxLon, maxLat, 500);
  }

  @Test
  void shouldThrowInvalidBoundingBoxException_whenMinLonIsGreaterThanMaxLon() {
    // minLon (-5.0) es mayor que maxLon (-6.0)
    assertThrows(
        InvalidBoundingBoxException.class,
        () -> storeService.findStoresInBoundingBox(-5.0, 37.0, -6.0, 38.0));

    // Verificamos que NUNCA se llama al repositorio si la validación falla
    verify(storeRepository, times(0))
        .findStoresInBoundingBox(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyInt());
  }

  @Test
  void shouldThrowInvalidBoundingBoxException_whenMinLatIsGreaterThanMaxLat() {
    // minLat (38.0) es mayor que maxLat (37.0)
    assertThrows(
        InvalidBoundingBoxException.class,
        () -> storeService.findStoresInBoundingBox(-6.0, 38.0, -5.0, 37.0));

    verify(storeRepository, times(0))
        .findStoresInBoundingBox(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyInt());
  }

  @Test
  void shouldFollowStore() {
    when(userService.getCurrentClient()).thenReturn(TEST_CLIENT);
    when(storeRepository.findById(TEST_STORE_ID)).thenReturn(Optional.of(TEST_STORE));
    when(storeFollowerRepository.save(any())).thenReturn(null);

    StoreFollower follow = storeService.followStore(TEST_STORE_ID);

    assertEquals(follow.getClient().getId(), TEST_CLIENT.getId());
    assertEquals(follow.getStore().getId(), TEST_STORE_ID);
    verify(storeFollowerRepository).save(follow);
  }

  @Test
  void shouldThrowExceptionIfUnfollowNotFollowedStore() {
    when(userService.getCurrentClient()).thenReturn(TEST_CLIENT);
    when(storeFollowerRepository.findByClientIdAndStoreId(TEST_CLIENT.getId(), TEST_STORE_ID))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> storeService.unfollowStore(TEST_STORE_ID));
    verify(storeFollowerRepository, never()).delete(any());
  }

  @Test
  void shouldUnfollowStore() {
    when(userService.getCurrentClient()).thenReturn(TEST_CLIENT);
    when(storeFollowerRepository.findByClientIdAndStoreId(TEST_CLIENT.getId(), TEST_STORE_ID))
        .thenReturn(Optional.of(TEST_FOLLOWER));
    doNothing().when(storeFollowerRepository).delete(any());

    storeService.unfollowStore(TEST_STORE_ID);

    verify(storeFollowerRepository, times(1)).delete(TEST_FOLLOWER);
  }

  @Test
  void shouldGetZeroFollowedStore() {
    when(userService.getCurrentClient()).thenReturn(TEST_CLIENT);
    when(storeFollowerRepository.findByClientId(TEST_CLIENT.getId())).thenReturn(List.of());

    List<StoreDTO> stores = storeService.getMyFollowedStores();

    assertEquals(stores.size(), 0);
  }

  @Test
  void shouldGetOneFollowedStore() {
    when(userService.getCurrentClient()).thenReturn(TEST_CLIENT);
    when(storeFollowerRepository.findByClientId(TEST_CLIENT.getId()))
        .thenReturn(List.of(TEST_FOLLOWER));

    List<StoreDTO> stores = storeService.getMyFollowedStores();

    assertEquals(stores.size(), 1);
    assertEquals(stores.get(0).getId(), new StoreDTO(TEST_STORE).getId());
  }

  @Test
  void shouldGetThreeFollowedStore() {
    when(userService.getCurrentClient()).thenReturn(TEST_CLIENT);
    when(storeFollowerRepository.findByClientId(TEST_CLIENT.getId()))
        .thenReturn(List.of(TEST_FOLLOWER, TEST_FOLLOWER, TEST_FOLLOWER));

    List<StoreDTO> stores = storeService.getMyFollowedStores();

    assertEquals(stores.size(), 3);
    assertEquals(stores.get(0).getId(), new StoreDTO(TEST_STORE).getId());
  }
}
