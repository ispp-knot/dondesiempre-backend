package ispp.project.dondesiempre.modules.stores.services;

import ispp.project.dondesiempre.modules.auth.services.AuthService;
import ispp.project.dondesiempre.modules.auth.services.UserService;
import ispp.project.dondesiempre.modules.clients.models.Client;
import ispp.project.dondesiempre.modules.common.exceptions.InvalidBoundingBoxException;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.common.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.modules.follows.models.StoreFollower;
import ispp.project.dondesiempre.modules.follows.repositories.StoreFollowerRepository;
import ispp.project.dondesiempre.modules.stores.dtos.StoreDTO;
import ispp.project.dondesiempre.modules.stores.dtos.StoreSocialNetworkDTO;
import ispp.project.dondesiempre.modules.stores.dtos.StoreUpdateDTO;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.repositories.StoreRepository;
import ispp.project.dondesiempre.modules.stores.repositories.StoreSocialNetworkRepository;
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

  @Transactional(readOnly = true, rollbackFor = InvalidBoundingBoxException.class)
  public List<Store> findStoresInBoundingBox(
      double minLon, double minLat, double maxLon, double maxLat)
      throws InvalidBoundingBoxException {
    if (minLon > maxLon || minLat > maxLat)
      throw new InvalidBoundingBoxException(
          "Invalid bounding box parameters: minimum coordinates cannot be greater than maximum coordinates.");
    return storeRepository.findStoresInBoundingBox(minLon, minLat, maxLon, maxLat, 500);
  }

  public List<StoreDTO> findAll() {
    List<Store> stores = storeRepository.findAll();
    return stores.stream().map(StoreDTO::new).toList();
  }

  @Transactional(rollbackFor = {UnauthorizedException.class, ResourceNotFoundException.class})
  public StoreDTO updateStore(UUID id, StoreUpdateDTO dto)
      throws UnauthorizedException, ResourceNotFoundException {
    Store storeToUpdate;

    storeToUpdate = applicationContext.getBean(StoreService.class).findById(id);
    authService.assertUserOwnsStore(storeToUpdate);

    if (dto.getName() != null) storeToUpdate.setName(dto.getName());
    if (dto.getEmail() != null) storeToUpdate.setEmail(dto.getEmail());
    if (dto.getAddress() != null) storeToUpdate.setAddress(dto.getAddress());
    if (dto.getOpeningHours() != null) storeToUpdate.setOpeningHours(dto.getOpeningHours());
    if (dto.getPhone() != null) storeToUpdate.setPhone(dto.getPhone());
    if (dto.getAboutUs() != null) storeToUpdate.setAboutUs(dto.getAboutUs());

    return new StoreDTO(storeRepository.save(storeToUpdate));
  }

  @Transactional(rollbackFor = {UnauthorizedException.class, ResourceNotFoundException.class})
  public StoreFollower followStore(UUID storeId)
      throws UnauthorizedException, ResourceNotFoundException {
    Client currentClient = userService.getCurrentClient();

    StoreFollower follow = new StoreFollower();
    follow.setClient(currentClient);
    follow.setStore(applicationContext.getBean(StoreService.class).findById(storeId));

    StoreFollower createdFollow = storeFollowerRepository.save(follow);
    return createdFollow;
  }

  @Transactional(rollbackFor = {UnauthorizedException.class, ResourceNotFoundException.class})
  public void unfollowStore(UUID storeId) throws UnauthorizedException, ResourceNotFoundException {
    Client currentClient = userService.getCurrentClient();

    StoreFollower follow =
        storeFollowerRepository
            .findByClientIdAndStoreId(currentClient.getId(), storeId)
            .orElseThrow(() -> new ResourceNotFoundException("You don't follow that store."));

    storeFollowerRepository.delete(follow);
  }

  @Transactional(
      readOnly = true,
      rollbackFor = {UnauthorizedException.class, ResourceNotFoundException.class})
  public List<Store> getMyFollowedStores() throws UnauthorizedException, ResourceNotFoundException {
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
