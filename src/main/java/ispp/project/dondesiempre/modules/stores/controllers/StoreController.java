package ispp.project.dondesiempre.modules.stores.controllers;

import ispp.project.dondesiempre.modules.auth.services.UserService;
import ispp.project.dondesiempre.modules.clients.models.Client;
import ispp.project.dondesiempre.modules.follows.dtos.StoreFollowerDTO;
import ispp.project.dondesiempre.modules.stores.dtos.StoreDTO;
import ispp.project.dondesiempre.modules.stores.dtos.StoreUpdateDTO;
import ispp.project.dondesiempre.modules.stores.services.StoreService;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StoreController {
  private final StoreService storeService;
  private final UserService userService;

  @GetMapping("/stores")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<StoreDTO>> searchStoresInBoundingBox(
      @RequestParam double minLon,
      @RequestParam double minLat,
      @RequestParam double maxLon,
      @RequestParam double maxLat) {
    return new ResponseEntity<>(
        storeService.findStoresInBoundingBox(minLon, minLat, maxLon, maxLat).stream()
            .map(s -> storeService.toDTO(s))
            .toList(),
        HttpStatus.OK);
  }

  @GetMapping("/stores/all")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<StoreDTO>> getStores() {
    return new ResponseEntity<>(storeService.findAll(), HttpStatus.OK);
  }

  @GetMapping("/stores/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<StoreDTO> getStoreById(@PathVariable UUID id) {
    return new ResponseEntity<>(storeService.toDTO(storeService.findById(id)), HttpStatus.OK);
  }

  @PutMapping("/stores/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<StoreDTO> updateStore(
      @PathVariable("id") UUID id, @RequestBody StoreUpdateDTO dto) {
    return new ResponseEntity<>(storeService.updateStore(id, dto), HttpStatus.OK);
  }

  @PostMapping("/stores/{storeId}/followers")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<String> followStore(@PathVariable("storeId") UUID storeId) {
    storeService.followStore(storeId);
    return new ResponseEntity<>("Store followed successfully", HttpStatus.CREATED);
  }

  @DeleteMapping("/stores/{storeId}/followers/me")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<String> unfollowStore(@PathVariable("storeId") UUID storeId) {
    storeService.unfollowStore(storeId);
    return new ResponseEntity<>("Store unfollowed successfully.", HttpStatus.OK);
  }

  @GetMapping("/clients/me/followed-stores")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<StoreDTO>> getMyFollowedStores() {
    List<StoreDTO> followedStores = storeService.getMyFollowedStores().stream().map(storeService::toDTO).toList();
    return new ResponseEntity<>(followedStores, HttpStatus.OK);
  }

  @GetMapping("/stores/{storeId}/followers/me")
  public ResponseEntity<StoreFollowerDTO> checkIfIFollowStore(@PathVariable UUID storeId) {
    Client currentClient = userService.getCurrentClient();
    boolean follows = storeService.checkIfClientFollowsStore(currentClient.getId(), storeId);
    return new ResponseEntity<>(
        new StoreFollowerDTO(currentClient.getId(), storeId, follows), HttpStatus.OK);
  }
}
