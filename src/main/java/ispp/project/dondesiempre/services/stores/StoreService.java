package ispp.project.dondesiempre.services.stores;

import ispp.project.dondesiempre.exceptions.InvalidBoundingBoxException;
import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.models.stores.dto.StoreDTO;
import ispp.project.dondesiempre.models.stores.dto.StoreSocialNetworkDTO;
import ispp.project.dondesiempre.repositories.stores.StoreRepository;
import ispp.project.dondesiempre.repositories.stores.StoreSocialNetworkRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreService {
  private final StoreRepository storeRepository;
  private final StoreSocialNetworkRepository socialNetworkRepository;

  @Transactional(readOnly = true, rollbackFor = ResourceNotFoundException.class)
  public Store findById(UUID id) throws ResourceNotFoundException {
    return storeRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Store with ID " + id + " not found."));
  }

  @Transactional(readOnly = true, rollbackFor = ResourceNotFoundException.class)
  public StoreDTO findByIdAsDTO(UUID id) throws ResourceNotFoundException {
    Store store = findById(id);
    StoreDTO dto = new StoreDTO(store);
    dto.setSocialNetworks(
        socialNetworkRepository.findByStore(store).stream()
            .map(StoreSocialNetworkDTO::new)
            .toList());
    return dto;
  }

  @Transactional(readOnly = true)
  public List<StoreDTO> findStoresInBoundingBox(
      double minLon, double minLat, double maxLon, double maxLat) {
    if (minLon > maxLon || minLat > maxLat)
      throw new InvalidBoundingBoxException(
          "Invalid bounding box parameters: minimum coordinates cannot be greater than maximum coordinates.");
    List<Store> stores =
        storeRepository.findStoresInBoundingBox(minLon, minLat, maxLon, maxLat, 500);
    return stores.stream().map(StoreDTO::new).toList();
  }
}
