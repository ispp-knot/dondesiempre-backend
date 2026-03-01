package ispp.project.dondesiempre.controllers.outfits;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ispp.project.dondesiempre.models.outfits.Outfit;
import ispp.project.dondesiempre.models.outfits.dto.OutfitCreationDTO;
import ispp.project.dondesiempre.models.outfits.dto.OutfitCreationProductDTO;
import ispp.project.dondesiempre.models.outfits.dto.OutfitDTO;
import ispp.project.dondesiempre.models.outfits.dto.OutfitProductDTO;
import ispp.project.dondesiempre.models.outfits.dto.OutfitUpdateDTO;
import ispp.project.dondesiempre.services.outfits.OutfitService;
import ispp.project.dondesiempre.services.products.ProductService;
import ispp.project.dondesiempre.services.storefronts.StorefrontService;
import ispp.project.dondesiempre.services.stores.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OutfitController {
  private final OutfitService outfitService;
  private final ProductService productService;
  private final StoreService storeService;
  private final StorefrontService storefrontService;

  @GetMapping("outfits/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<OutfitDTO> getById(@PathVariable("id") UUID id) {
    Outfit outfit = outfitService.findById(id);
    OutfitDTO dto =
        OutfitDTO.from(
            outfit,
            outfitService.findTagsByOutfitId(id),
            outfitService.findOutfitProductsByOutfitId(id));
    return new ResponseEntity<>(dto, HttpStatus.OK);
  }

  @GetMapping("storefronts/{storefrontId}/outfits")
  @ResponseStatus(HttpStatus.FOUND)
  public ResponseEntity<List<OutfitDTO>> getByStorefrontId(
      @PathVariable("storefrontId") UUID storefrontId) {
    List<OutfitDTO> dtos =
        outfitService.findByStorefront(storefrontService.findById(storefrontId)).stream()
            .map(
                outfit ->
                    OutfitDTO.from(
                        outfit,
                        outfitService.findTagsByOutfitId(outfit.getId()),
                        outfitService.findOutfitProductsByOutfitId(outfit.getId())))
            .toList();
    return new ResponseEntity<>(dtos, HttpStatus.OK);
  }

  @GetMapping("stores/{storeId}/outfits")
  @ResponseStatus(HttpStatus.FOUND)
  public ResponseEntity<List<OutfitDTO>> getByStoreId(@PathVariable("storeId") UUID storeId) {
    List<OutfitDTO> dtos =
        outfitService.findByStore(storeService.findById(storeId)).stream()
            .map(
                outfit ->
                    OutfitDTO.from(
                        outfit,
                        outfitService.findTagsByOutfitId(outfit.getId()),
                        outfitService.findOutfitProductsByOutfitId(outfit.getId())))
            .toList();
    return new ResponseEntity<>(dtos, HttpStatus.OK);
  }

  @PostMapping("outfits")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<OutfitDTO> create(@RequestBody @Valid OutfitCreationDTO dto) {
    Outfit outfit = outfitService.create(dto);
    OutfitDTO outfitDTO =
        OutfitDTO.from(
            outfit,
            outfitService.findTagsByOutfitId(outfit.getId()),
            outfitService.findOutfitProductsByOutfitId(outfit.getId()));
    return new ResponseEntity<>(outfitDTO, HttpStatus.CREATED);
  }

  @PutMapping("outfits/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<OutfitDTO> update(
      @PathVariable("id") UUID id, @RequestBody @Valid OutfitUpdateDTO dto) {
    Outfit outfit = outfitService.update(id, dto);
    OutfitDTO outfitDTO =
        OutfitDTO.from(
            outfit,
            outfitService.findTagsByOutfitId(id),
            outfitService.findOutfitProductsByOutfitId(id));
    return new ResponseEntity<>(outfitDTO, HttpStatus.OK);
  }

  @PostMapping("outfits/{id}/tags")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<String> addTag(@PathVariable("id") UUID id, @RequestBody String tag) {
    return new ResponseEntity<>(outfitService.addTag(id, tag), HttpStatus.CREATED);
  }

  @DeleteMapping("outfits/{id}/tags")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<String> removeTag(@PathVariable("id") UUID id, @RequestBody String tag) {
    outfitService.removeTag(id, tag);
    return new ResponseEntity<>("Tag successfully removed from outfit.", HttpStatus.OK);
  }

  @PostMapping("outfits/{id}/products")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<OutfitProductDTO> addProduct(
      @PathVariable("id") UUID id, @RequestBody OutfitCreationProductDTO dto) {
    return new ResponseEntity<>(
        OutfitProductDTO.from(outfitService.addProduct(id, dto)), HttpStatus.CREATED);
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
      @PathVariable("id") UUID id, @RequestBody List<OutfitCreationProductDTO> products) {
    outfitService.sortProducts(id, products);
    return new ResponseEntity<>("Products successfully sorted.", HttpStatus.OK);
  }

  @DeleteMapping("outfits/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<String> delete(@PathVariable("id") UUID id) {
    outfitService.delete(id);
    return new ResponseEntity<>("Outfit successfully deleted.", HttpStatus.OK);
  }
}
