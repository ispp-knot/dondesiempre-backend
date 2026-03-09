package ispp.project.dondesiempre.modules.follows.controllers;

import ispp.project.dondesiempre.modules.auth.services.UserService;
import ispp.project.dondesiempre.modules.clients.models.Client;
import ispp.project.dondesiempre.modules.follows.dtos.StoreFollowerDTO;
import ispp.project.dondesiempre.modules.follows.services.StoreFollowerService;
import ispp.project.dondesiempre.modules.stores.dtos.StoreDTO;
import ispp.project.dondesiempre.modules.stores.services.StoreService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StoreFollowerController {

  private final StoreFollowerService followersService;
  private final StoreService storeService;
  private final UserService userService;

  @GetMapping("/clients/me/following")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<StoreDTO>> getMyFollowedStores() {
    List<StoreDTO> followedStores =
        followersService.getMyFollowedStores().stream().map(storeService::toDTO).toList();
    return new ResponseEntity<>(followedStores, HttpStatus.OK);
  }

  @GetMapping("/stores/{storeId}/follow")
  public ResponseEntity<StoreFollowerDTO> checkIfIFollowStore(@PathVariable UUID storeId) {
    Client currentClient = userService.getCurrentClient();
    boolean follows = followersService.checkIfClientFollowsStore(currentClient.getId(), storeId);
    return new ResponseEntity<>(
        new StoreFollowerDTO(currentClient.getId(), storeId, follows), HttpStatus.OK);
  }

  @PostMapping("/stores/{storeId}/followers")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<String> followStore(@PathVariable("storeId") UUID storeId) {
    followersService.followStore(storeId);
    return new ResponseEntity<>("Store followed successfully", HttpStatus.CREATED);
  }

  @DeleteMapping("/stores/{storeId}/follow")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<String> unfollowStore(@PathVariable("storeId") UUID storeId) {
    followersService.unfollowStore(storeId);
    return new ResponseEntity<>("Store unfollowed successfully.", HttpStatus.OK);
  }
}
