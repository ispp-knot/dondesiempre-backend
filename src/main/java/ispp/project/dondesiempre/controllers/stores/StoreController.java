package ispp.project.dondesiempre.controllers.stores;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import ispp.project.dondesiempre.models.stores.dto.StoreDTO;
import ispp.project.dondesiempre.models.stores.dto.StoreUpdateDTO;
import ispp.project.dondesiempre.services.stores.StoreService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
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

  // Para pruebas
  @GetMapping("/storeses")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<StoreDTO>> getStores() {
    return new ResponseEntity<>(storeService.findAll(), HttpStatus.OK);
  }

  //

  @GetMapping("/stores/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<StoreDTO> getById(@PathVariable("id") UUID id) {
    return new ResponseEntity<>(storeService.findByIdToDTO(id), HttpStatus.OK);
  }

  @PutMapping("/stores/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<StoreDTO> updateStore(
      @PathVariable("id") UUID id, @RequestBody StoreUpdateDTO dto) {
    return new ResponseEntity<>(storeService.updateStore(id, dto), HttpStatus.OK);
  }
}
