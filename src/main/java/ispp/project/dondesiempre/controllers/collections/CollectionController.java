package ispp.project.dondesiempre.controllers.collections;

import ispp.project.dondesiempre.models.collections.dto.CollectionCreationDTO;
import ispp.project.dondesiempre.models.collections.dto.CollectionResponseDTO;
import ispp.project.dondesiempre.models.collections.dto.CollectionUpdateDTO;
import ispp.project.dondesiempre.services.collections.CollectionService;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CollectionController {

  private final CollectionService collectionService;

  @GetMapping("/store/{storeId}/collections")
  public ResponseEntity<List<CollectionResponseDTO>> getByStore(@PathVariable UUID storeId) {
    return ResponseEntity.ok(
        collectionService.getByStore(storeId).stream().map(CollectionResponseDTO::new).toList());
  }

  @GetMapping("/collections/{id}")
  public ResponseEntity<CollectionResponseDTO> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(new CollectionResponseDTO(collectionService.getById(id)));
  }

  @PostMapping("/store/{storeId}/collections")
  public ResponseEntity<CollectionResponseDTO> create(
      @PathVariable UUID storeId, @Valid @RequestBody CollectionCreationDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new CollectionResponseDTO(collectionService.create(storeId, dto)));
  }

  @PutMapping("/collections/{id}")
  public ResponseEntity<CollectionResponseDTO> update(
      @PathVariable UUID id, @Valid @RequestBody CollectionUpdateDTO dto) {
    return ResponseEntity.ok(new CollectionResponseDTO(collectionService.update(id, dto)));
  }

  @DeleteMapping("/collections/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    collectionService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
