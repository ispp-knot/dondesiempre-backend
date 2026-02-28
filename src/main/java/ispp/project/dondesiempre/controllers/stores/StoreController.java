package ispp.project.dondesiempre.controllers.stores;

import ispp.project.dondesiempre.models.stores.StoreFollower;
import ispp.project.dondesiempre.models.stores.dto.StoreDTO;
import ispp.project.dondesiempre.services.stores.StoreService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StoreController {
  private final StoreService storeService;

  @GetMapping("/stores")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<StoreDTO>> searchStoresInBoundingBox(
      @RequestParam double minLon,
      @RequestParam double minLat,
      @RequestParam double maxLon,
      @RequestParam double maxLat) {
    return new ResponseEntity<>(
        storeService.findStoresInBoundingBox(minLon, minLat, maxLon, maxLat), HttpStatus.OK);
  }

  @PostMapping("/stores/{storeId}/followers")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<StoreFollower> followStore(@PathVariable("storeId") UUID storeId) {
    StoreFollower follow = storeService.followStore(storeId);
    return new ResponseEntity<>(follow, HttpStatus.CREATED);
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
    List<StoreDTO> followedStores = storeService.getMyFollowedStores();
    return new ResponseEntity<>(followedStores, HttpStatus.OK);
  }
}
