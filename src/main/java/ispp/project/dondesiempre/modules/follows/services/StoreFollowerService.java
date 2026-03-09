package ispp.project.dondesiempre.modules.follows.services;

import ispp.project.dondesiempre.modules.auth.services.UserService;
import ispp.project.dondesiempre.modules.clients.models.Client;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.common.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.modules.follows.models.StoreFollower;
import ispp.project.dondesiempre.modules.follows.repositories.StoreFollowerRepository;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.services.StoreService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreFollowerService {

  private final StoreFollowerRepository followersRepository;
  private final UserService userService;
  private final StoreService storeService;

  @Transactional(rollbackFor = {UnauthorizedException.class, ResourceNotFoundException.class})
  public StoreFollower followStore(UUID storeId)
      throws UnauthorizedException, ResourceNotFoundException {
    Client currentClient = userService.getCurrentClient();

    StoreFollower follow = new StoreFollower();
    follow.setClient(currentClient);
    follow.setStore(storeService.findById(storeId));

    StoreFollower createdFollow = followersRepository.save(follow);
    return createdFollow;
  }

  @Transactional(readOnly = true)
  public boolean checkIfClientFollowsStore(UUID clientId, UUID storeId) {
    return followersRepository.existsByClientIdAndStoreId(clientId, storeId);
  }

  @Transactional(
      readOnly = true,
      rollbackFor = {UnauthorizedException.class, ResourceNotFoundException.class})
  public List<Store> getMyFollowedStores() throws UnauthorizedException, ResourceNotFoundException {
    Client currentClient = userService.getCurrentClient();
    return followersRepository.findByClientId(currentClient.getId()).stream()
        .map(follower -> follower.getStore())
        .toList();
  }

  @Transactional(rollbackFor = {UnauthorizedException.class, ResourceNotFoundException.class})
  public void unfollowStore(UUID storeId) throws UnauthorizedException, ResourceNotFoundException {
    Client currentClient = userService.getCurrentClient();

    StoreFollower follow =
        followersRepository
            .findByClientIdAndStoreId(currentClient.getId(), storeId)
            .orElseThrow(() -> new ResourceNotFoundException("You don't follow that store."));

    followersRepository.delete(follow);
  }
}
