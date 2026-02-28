package ispp.project.dondesiempre.controllers.outfits;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import ispp.project.dondesiempre.models.outfits.dto.OutfitCreationDTO;
import ispp.project.dondesiempre.models.outfits.dto.OutfitCreationProductDTO;
import ispp.project.dondesiempre.models.outfits.dto.OutfitDTO;
import ispp.project.dondesiempre.models.outfits.dto.OutfitProductDTO;
import ispp.project.dondesiempre.models.outfits.dto.OutfitUpdateDTO;
import ispp.project.dondesiempre.services.outfits.OutfitService;
import ispp.project.dondesiempre.services.stores.StoreService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OutfitController {
  private final OutfitService outfitService;
  private final StoreService storeService;

  @GetMapping("outfits/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<OutfitDTO> getById(@PathVariable("id") UUID id) {
    return new ResponseEntity<>(outfitService.findByIdToDTO(id), HttpStatus.OK);
  }

  @GetMapping("stores/{storeId}/outfits")
  @ResponseStatus(HttpStatus.FOUND)
  public ResponseEntity<List<OutfitDTO>> getByStoreId(@PathVariable("storeId") UUID storeId) {
    return new ResponseEntity<>(
        outfitService.findByStore(storeService.findById(storeId)), HttpStatus.OK);
  }

  @PostMapping("outfits")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<OutfitDTO> create(@RequestBody @Valid OutfitCreationDTO dto) {
    return new ResponseEntity<>(outfitService.create(dto), HttpStatus.CREATED);
  }

  @PutMapping("outfits/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<OutfitDTO> update(
      @PathVariable("id") UUID id, @RequestBody @Valid OutfitUpdateDTO dto) {
    return new ResponseEntity<>(outfitService.update(id, dto), HttpStatus.OK);
  }

  @PostMapping("outfits/{id}/tags")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<String> addTag(@PathVariable("id") UUID id, @RequestBody String tag) {
    return new ResponseEntity<>(outfitService.addTag(id, tag), HttpStatus.CREATED);
  }

  @PostMapping("outfits/{id}/products")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<OutfitProductDTO> addProduct(
      @PathVariable("id") UUID id, @RequestBody OutfitCreationProductDTO dto) {
    return new ResponseEntity<>(outfitService.addProduct(id, dto), HttpStatus.CREATED);
  }

  @DeleteMapping("outfits/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<String> delete(@PathVariable("id") UUID id) {
    return new ResponseEntity<>("Outfit successfully deleted.", HttpStatus.OK);
  }
}
