package ispp.project.dondesiempre.modules.stores.controllers;

import ispp.project.dondesiempre.modules.stores.dtos.StoreImageDTO;
import ispp.project.dondesiempre.modules.stores.dtos.StoreImageUpdateDTO;
import ispp.project.dondesiempre.modules.stores.services.StoreImageService;
import ispp.project.dondesiempre.modules.stores.services.StoreService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StoreImageController {

  private final StoreImageService storeImageService;
  private final StoreService storeService;

  @GetMapping("stores/{storeId}/images")
  public ResponseEntity<List<StoreImageDTO>> getByStore(@PathVariable UUID storeId) {
    storeService.findById(storeId);
    List<StoreImageDTO> images =
        storeImageService.findImageByStoreId(storeId).stream().map(StoreImageDTO::new).toList();
    return new ResponseEntity<>(images, HttpStatus.OK);
  }

  @PostMapping(value = "stores/{storeId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<StoreImageDTO> create(
      @PathVariable UUID storeId,
      @RequestPart("dto") @Valid StoreImageUpdateDTO dto,
      @RequestPart("image") MultipartFile image) {

    StoreImageDTO createdImage = storeImageService.add(storeId, dto, image);

    return new ResponseEntity<>(createdImage, HttpStatus.CREATED);
  }

  @PutMapping("stores/{storeId}/images/{id}")
  public ResponseEntity<StoreImageDTO> update(
      @PathVariable UUID storeId,
      @PathVariable UUID id,
      @RequestBody @Valid StoreImageUpdateDTO dto) {

    StoreImageDTO image = storeImageService.update(id, dto);

    return new ResponseEntity<>(image, HttpStatus.OK);
  }

  @DeleteMapping("stores/{storeId}/images/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<String> delete(@PathVariable UUID storeId, @PathVariable UUID id) {

    storeImageService.delete(id);

    return new ResponseEntity<>("Image successfully removed.", HttpStatus.OK);
  }
}
