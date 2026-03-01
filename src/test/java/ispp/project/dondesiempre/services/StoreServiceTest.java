package ispp.project.dondesiempre.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ispp.project.dondesiempre.exceptions.InvalidBoundingBoxException;
import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.models.stores.dto.StoreDTO;
import ispp.project.dondesiempre.repositories.stores.StoreRepository;
import ispp.project.dondesiempre.services.stores.StoreService;
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
public class StoreServiceTest {

  @Mock private StoreRepository storeRepository;

  @InjectMocks private StoreService storeService;

  private Store store;
  private UUID storeId;

  @BeforeEach
  void setUp() {
    store = new Store();
    storeId = UUID.randomUUID();
    store.setId(storeId);
    store.setName("Tienda de Prueba");
  }

  @Test
  void shouldReturnStore_whenFindByIdExists() {
    when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

    Store result = storeService.findById(storeId);

    assertEquals("Tienda de Prueba", result.getName());
    verify(storeRepository, times(1)).findById(storeId);
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
        .thenReturn(List.of(store));

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
}
