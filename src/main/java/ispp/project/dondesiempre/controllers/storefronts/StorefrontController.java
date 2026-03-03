package ispp.project.dondesiempre.controllers.storefronts;

import ispp.project.dondesiempre.models.storefronts.Storefront;
import ispp.project.dondesiempre.models.storefronts.dto.StorefrontDTO;
import ispp.project.dondesiempre.services.UserService;
import ispp.project.dondesiempre.services.storefronts.StorefrontService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/storefronts")
@RequiredArgsConstructor
public class StorefrontController {
  private final StorefrontService storefrontService;
  private final UserService userService;

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<StorefrontDTO> getStorefrontById(@PathVariable UUID id) {
    return new ResponseEntity<>(new StorefrontDTO(storefrontService.findById(id)), HttpStatus.OK);
  }

  @PutMapping("/{id}")
  public ResponseEntity<StorefrontDTO> updateStorefront(
      @PathVariable UUID id, @RequestBody StorefrontDTO storefrontDTO) {
    Storefront storefront = storefrontService.findById(id);
    userService.assertUserOwnsStore(storefront.getStore());
    StorefrontDTO updated = storefrontService.updateStorefront(id, storefrontDTO);
    return ResponseEntity.ok(updated);
  }
}
