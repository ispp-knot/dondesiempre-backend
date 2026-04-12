package ispp.project.dondesiempre.modules.outfits.controllers;

import ispp.project.dondesiempre.modules.outfits.dtos.OutfitCreationDTO;
import ispp.project.dondesiempre.modules.outfits.dtos.OutfitCreationProductDTO;
import ispp.project.dondesiempre.modules.outfits.dtos.OutfitDTO;
import ispp.project.dondesiempre.modules.outfits.dtos.OutfitProductDTO;
import ispp.project.dondesiempre.modules.outfits.dtos.OutfitSortDTO;
import ispp.project.dondesiempre.modules.outfits.dtos.OutfitTagDTO;
import ispp.project.dondesiempre.modules.outfits.dtos.OutfitUpdateDTO;
import ispp.project.dondesiempre.modules.outfits.models.Outfit;
import ispp.project.dondesiempre.modules.outfits.services.OutfitService;
import ispp.project.dondesiempre.modules.products.services.ProductService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OutfitController {
  private final OutfitService outfitService;
  private final ProductService productService;
  private final StoreService storeService;

  @GetMapping("outfits/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<OutfitDTO> getById(@PathVariable("id") UUID id) {
    return new ResponseEntity<>(outfitService.findByIdAsDTO(id), HttpStatus.OK);
  }

  @GetMapping("stores/{storeId}/outfits")
  @ResponseStatus(HttpStatus.FOUND)
  public ResponseEntity<List<OutfitDTO>> getByStoreId(
      @PathVariable("storeId") UUID storeId,
      @RequestParam(value = "name", required = false) String name) {
    storeService.findById(storeId);
    return new ResponseEntity<>(
        outfitService.findByStoreIdAndNameAsDTO(storeId, name), HttpStatus.OK);
  }

  @PostMapping(value = "stores/{storeId}/outfits", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<OutfitDTO> create(
      @PathVariable("storeId") UUID storeId,
      @RequestPart("dto") @Valid OutfitCreationDTO dto,
      @RequestPart(value = "image", required = false) MultipartFile image) {
    Outfit outfit = outfitService.create(storeId, dto, image);
    OutfitDTO outfitDTO =
        new OutfitDTO(
            outfit,
            outfitService.findTagsByOutfitId(outfit.getId()),
            outfitService.findOutfitProductsByOutfitId(outfit.getId()));
    return new ResponseEntity<>(outfitDTO, HttpStatus.CREATED);
  }

  @PutMapping(value = "outfits/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<OutfitDTO> update(
      @PathVariable("id") UUID id,
      @RequestPart("dto") @Valid OutfitUpdateDTO dto,
      @RequestPart(value = "image", required = false) MultipartFile image) {
    Outfit outfit = outfitService.update(id, dto, image);
    OutfitDTO outfitDTO =
        new OutfitDTO(
            outfit,
            outfitService.findTagsByOutfitId(id),
            outfitService.findOutfitProductsByOutfitId(id));
    return new ResponseEntity<>(outfitDTO, HttpStatus.OK);
  }

  @PostMapping("outfits/{id}/tags")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<OutfitTagDTO> addTag(
      @PathVariable("id") UUID id, @RequestBody @Valid OutfitTagDTO tagDTO) {
    return new ResponseEntity<>(outfitService.addTag(id, tagDTO), HttpStatus.CREATED);
  }

  @DeleteMapping("outfits/{id}/tags")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<String> removeTag(
      @PathVariable("id") UUID id, @RequestBody @Valid OutfitTagDTO tagDTO) {
    outfitService.removeTag(id, tagDTO);
    return new ResponseEntity<>("Tag successfully removed from outfit.", HttpStatus.OK);
  }

  @PostMapping("outfits/{id}/products")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<OutfitProductDTO> addProduct(
      @PathVariable("id") UUID id, @RequestBody @Valid OutfitCreationProductDTO dto) {
    return new ResponseEntity<>(
        new OutfitProductDTO(outfitService.addProduct(id, dto)), HttpStatus.CREATED);
  }

  @DeleteMapping("outfits/{id}/products/{productId}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<String> removeProduct(
      @PathVariable("id") UUID id, @PathVariable("productId") UUID productId) {
    outfitService.removeProduct(id, productService.getProductById(productId));
    return new ResponseEntity<>("Product successfully removed from outfit.", HttpStatus.CREATED);
  }

  @PatchMapping("outfits/{id}/products/sort")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<String> sortProducts(
      @PathVariable("id") UUID id, @RequestBody @Valid List<OutfitCreationProductDTO> products) {
    outfitService.sortProducts(id, products);
    return new ResponseEntity<>("Products successfully sorted.", HttpStatus.OK);
  }

  @DeleteMapping("outfits/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<String> delete(@PathVariable("id") UUID id) {
    outfitService.delete(id);
    return new ResponseEntity<>("Outfit successfully deleted.", HttpStatus.OK);
  }

  @PatchMapping("stores/{id}/outfits/sort")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<String> sortOutfits(
      @PathVariable("id") UUID id, @RequestBody @Valid List<OutfitSortDTO> outfits) {
    outfitService.sortOutfits(id, outfits);
    return new ResponseEntity<>("Outfits successfully sorted.", HttpStatus.OK);
  }
}
