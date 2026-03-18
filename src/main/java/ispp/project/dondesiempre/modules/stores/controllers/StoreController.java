package ispp.project.dondesiempre.modules.stores.controllers;

import ispp.project.dondesiempre.modules.stores.dtos.StoreDTO;
import ispp.project.dondesiempre.modules.stores.dtos.StoreUpdateDTO;
import ispp.project.dondesiempre.modules.stores.services.StoreService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

  @GetMapping("/stores")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<StoreDTO>> searchStores(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Double lat,
      @RequestParam(required = false) Double lon) {
    return new ResponseEntity<>(
<<<<<<< HEAD
        storeService.searchStores(name, lat, lon).stream()
            .map(store -> storeService.toDTO(store, lat, lon))
            .toList(),
        HttpStatus.OK);
=======
        storeService.findStoresInBoundingBoxAsDTO(minLon, minLat, maxLon, maxLat), HttpStatus.OK);
>>>>>>> origin/main
  }

  @GetMapping("/stores/map")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<StoreDTO>> getStoresInMap(
      @RequestParam Double minLon,
      @RequestParam Double minLat,
      @RequestParam Double maxLon,
      @RequestParam Double maxLat) {
    return new ResponseEntity<>(
        storeService.findStoresInBoundingBoxAsDTO(minLon, minLat, maxLon, maxLat), HttpStatus.OK);
  }

  @GetMapping("/stores/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<StoreDTO> getStoreById(@PathVariable UUID id) {
    return new ResponseEntity<>(storeService.toDTO(storeService.findById(id)), HttpStatus.OK);
  }

  @PutMapping("/stores/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<StoreDTO> updateStore(
      @PathVariable("id") UUID id, @RequestBody @Valid StoreUpdateDTO dto) {
    return new ResponseEntity<>(storeService.updateStore(id, dto), HttpStatus.OK);
  }
}
