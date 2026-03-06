package ispp.project.dondesiempre.services.stores;

import ispp.project.dondesiempre.exceptions.InvalidBoundingBoxException;
import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.models.Client;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.models.stores.StoreFollower;
import ispp.project.dondesiempre.models.stores.dto.StoreDTO;
import ispp.project.dondesiempre.models.stores.dto.StoreSocialNetworkDTO;
import ispp.project.dondesiempre.models.stores.dto.StoreUpdateDTO;
import ispp.project.dondesiempre.repositories.stores.StoreFollowerRepository;
import ispp.project.dondesiempre.repositories.stores.StoreRepository;
import ispp.project.dondesiempre.repositories.stores.StoreSocialNetworkRepository;
import ispp.project.dondesiempre.services.AuthService;
import ispp.project.dondesiempre.services.UserService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreService {
  private final StoreRepository storeRepository;
  private final StoreSocialNetworkRepository storeSocialNetworkRepository;
  private final ApplicationContext applicationContext;
  private final StoreFollowerRepository storeFollowerRepository;
  private final UserService userService;
  private final AuthService authService;

  @Transactional(readOnly = true, rollbackFor = ResourceNotFoundException.class)
  public Store findById(UUID id) throws ResourceNotFoundException {
    return storeRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Store with ID " + id + " not found."));
  }

  @Transactional(readOnly = true)
  public StoreDTO toDTO(Store store) {
    StoreDTO dto = new StoreDTO(store);
    dto.setSocialNetworks(
        storeSocialNetworkRepository.findByStoreId(store.getId()).stream()
            .map(StoreSocialNetworkDTO::new)
            .toList());
    return dto;
  }

  @Transactional(readOnly = true)
  public List<Store> findStoresInBoundingBox(
      double minLon, double minLat, double maxLon, double maxLat) {
    if (minLon > maxLon || minLat > maxLat)
      throw new InvalidBoundingBoxException(
          "Invalid bounding box parameters: minimum coordinates cannot be greater than maximum coordinates.");
    return storeRepository.findStoresInBoundingBox(minLon, minLat, maxLon, maxLat, 500);
  }

  public List<StoreDTO> findAll() {
    List<Store> stores = storeRepository.findAll();
    return stores.stream().map(StoreDTO::new).toList();
  }

  @Transactional(rollbackFor = ResourceNotFoundException.class)
  public StoreDTO updateStore(UUID id, StoreUpdateDTO dto) throws ResourceNotFoundException {
    Store storeToUpdate;

    storeToUpdate = applicationContext.getBean(StoreService.class).findById(id);
    authService.assertUserOwnsStore(storeToUpdate);

    if (dto.getName() != null) storeToUpdate.setName(dto.getName());
    if (dto.getEmail() != null) storeToUpdate.setEmail(dto.getEmail());
    if (dto.getStoreID() != null) storeToUpdate.setStoreID(dto.getStoreID());
    if (dto.getAddress() != null) storeToUpdate.setAddress(dto.getAddress());
    if (dto.getOpeningHours() != null) storeToUpdate.setOpeningHours(dto.getOpeningHours());
    if (dto.getPhone() != null) storeToUpdate.setPhone(dto.getPhone());
    if (dto.getAboutUs() != null) storeToUpdate.setAboutUs(dto.getAboutUs());

    return new StoreDTO(storeRepository.save(storeToUpdate));
  }

  @Transactional
  public StoreFollower followStore(UUID storeId) {
    Client currentClient = userService.getCurrentClient();

    StoreFollower follow = new StoreFollower();
    follow.setClient(currentClient);
    follow.setStore(findById(storeId));

    StoreFollower createdFollow = storeFollowerRepository.save(follow);
    return createdFollow;
  }

  @Transactional(rollbackFor = ResourceNotFoundException.class)
  public void unfollowStore(UUID storeId) throws ResourceNotFoundException {
    Client currentClient = userService.getCurrentClient();

    StoreFollower follow =
        storeFollowerRepository
            .findByClientIdAndStoreId(currentClient.getId(), storeId)
            .orElseThrow(() -> new ResourceNotFoundException("You don't follow that store."));

    storeFollowerRepository.delete(follow);
  }

  @Transactional(readOnly = true)
  public List<Store> getMyFollowedStores() {
    Client currentClient = userService.getCurrentClient();
    return storeFollowerRepository.findByClientId(currentClient.getId()).stream()
        .map(follower -> follower.getStore())
        .toList();
  }

  @Transactional(readOnly = true)
  public boolean checkIfClientFollowsStore(UUID clientId, UUID storeId) {
    return storeFollowerRepository.existsByClientIdAndStoreId(clientId, storeId);
  }
}
