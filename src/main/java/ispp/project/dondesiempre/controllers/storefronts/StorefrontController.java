package ispp.project.dondesiempre.controllers.storefronts;

import ispp.project.dondesiempre.models.storefronts.dto.StorefrontDTO;
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

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<StorefrontDTO> getStorefrontById(@PathVariable UUID id) {
    return new ResponseEntity<>(storefrontService.getDTOById(id), HttpStatus.OK);
  }

  // TODO: Quitar el /edit
  // TODO: Validar que el dueño del storefront es el que edita
  @PutMapping("/{id}")
  public ResponseEntity<StorefrontDTO> updateStorefront(
      @PathVariable UUID id, @RequestBody StorefrontDTO storefrontDTO) {

    StorefrontDTO updated = storefrontService.updateStorefront(id, storefrontDTO);
    return ResponseEntity.ok(updated);
  }
}
