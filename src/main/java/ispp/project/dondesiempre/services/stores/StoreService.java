package ispp.project.dondesiempre.services.stores;

import ispp.project.dondesiempre.exceptions.InvalidBoundingBoxException;
import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.models.Client;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.models.stores.StoreFollower;
import ispp.project.dondesiempre.models.stores.StoreSocialNetwork;
import ispp.project.dondesiempre.models.stores.dto.SocialNetworkDTO;
import ispp.project.dondesiempre.models.stores.dto.StoreDTO;
import ispp.project.dondesiempre.models.stores.dto.StoreUpdateDTO;
import ispp.project.dondesiempre.repositories.stores.StoreFollowerRepository;
import ispp.project.dondesiempre.repositories.stores.StoreRepository;
import ispp.project.dondesiempre.repositories.stores.StoreSocialNetworkRepository;
import ispp.project.dondesiempre.services.UserService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
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

  public StoreDTO findByIdToDTO(UUID id) {
    Store store =
        storeRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

    StoreDTO storeDTO = new StoreDTO(store);

    List<StoreSocialNetwork> networks = storeSocialNetworkRepository.findByStoreId(store.getId());

    List<SocialNetworkDTO> networkDTOs =
        networks.stream().map(SocialNetworkDTO::new).collect(Collectors.toList());

    storeDTO.setSocialNetworks(networkDTOs);

    return storeDTO;
  }

  public List<StoreDTO> findAll() {
    List<Store> stores = storeRepository.findAll();
    return stores.stream().map(StoreDTO::new).toList();
  }

  @Transactional(rollbackFor = ResourceNotFoundException.class)
  public StoreDTO updateStore(UUID id, StoreUpdateDTO dto) throws ResourceNotFoundException {
    Store storeToUpdate;

    storeToUpdate = applicationContext.getBean(StoreService.class).findById(id);

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
    Client currentUser = userService.getCurrentClient();

    StoreFollower follow = new StoreFollower();
    follow.setClient(currentUser);
    follow.setStore(findById(storeId));

    StoreFollower createdFollow = storeFollowerRepository.save(follow);
    return createdFollow;
  }

  @Transactional(rollbackFor = ResourceNotFoundException.class)
  public void unfollowStore(UUID storeId) throws ResourceNotFoundException {
    Client currentUser = userService.getCurrentClient();

    StoreFollower follow =
        storeFollowerRepository
            .findByClientIdAndStoreId(currentUser.getId(), storeId)
            .orElseThrow(() -> new ResourceNotFoundException("You don't follow that store."));

    storeFollowerRepository.delete(follow);
  }

  @Transactional(readOnly = true)
  public List<Store> getMyFollowedStores() {
    Client currentUser = userService.getCurrentClient();
    return storeFollowerRepository.findByClientId(currentUser.getId()).stream()
        .map(follower -> follower.getStore())
        .toList();
  }
}
