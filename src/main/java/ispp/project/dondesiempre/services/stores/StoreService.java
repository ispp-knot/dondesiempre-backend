package ispp.project.dondesiempre.services.stores;

import ispp.project.dondesiempre.exceptions.InvalidBoundingBoxException;
import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.models.Client;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.models.stores.StoreFollower;
import ispp.project.dondesiempre.models.stores.dto.StoreDTO;
import ispp.project.dondesiempre.repositories.stores.StoreFollowerRepository;
import ispp.project.dondesiempre.repositories.stores.StoreRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreService {
  private final StoreRepository storeRepository;
  private final StoreFollowerRepository storeFollowerRepository;

  @Transactional(readOnly = true, rollbackFor = ResourceNotFoundException.class)
  public Store findById(UUID id) throws ResourceNotFoundException {
    return storeRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Store with ID " + id + " not found."));
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

  Client mockCurrentUser() {
    UUID currentUserId = UUID.randomUUID();
    Client currentUser = new Client();
    currentUser.setId(currentUserId);
    currentUser.setName("Mock");
    currentUser.setSurname("Client");
    currentUser.setEmail("mock@user.test");
    return currentUser;
  }

  @Transactional
  public StoreFollower followStore(UUID storeId) {
    Client currentUser = mockCurrentUser();

    StoreFollower follow = new StoreFollower();
    follow.setClient(currentUser);
    follow.setStore(findById(storeId));

    storeFollowerRepository.save(follow);
    return follow;
  }

  @Transactional
  public void unfollowStore(UUID storeId) {
    Client currentUser = mockCurrentUser();

    StoreFollower follow =
        storeFollowerRepository
            .findByClientIdAndStoreId(currentUser.getId(), storeId)
            .orElseThrow(() -> new ResourceNotFoundException("You don't follow that store."));

    storeFollowerRepository.delete(follow);
  }
}
