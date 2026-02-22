package ispp.project.dondesiempre.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import ispp.project.dondesiempre.models.outfits.Outfit;
import ispp.project.dondesiempre.models.outfits.OutfitTag;
import ispp.project.dondesiempre.models.outfits.dto.OutfitCreationDTO;
import ispp.project.dondesiempre.models.outfits.dto.OutfitCreationProductDTO;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.services.OutfitService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/outfits")
public class OutfitController {
  private OutfitService outfitService;

  public OutfitController(OutfitService outfitService) {
    this.outfitService = outfitService;
  }

  @GetMapping("{id}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Outfit> getById(@PathVariable("id") Integer id) {
    return new ResponseEntity<>(outfitService.findById(id), HttpStatus.FOUND);
  }

  @GetMapping("store/{storeId}")
  @ResponseStatus(HttpStatus.FOUND)
  public ResponseEntity<List<Outfit>> getByStoreId(@PathVariable("storeId") Integer storeId) {
    return new ResponseEntity<>(outfitService.findByStore(storeId), HttpStatus.FOUND);
  }

  @PostMapping("")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<Outfit> create(@RequestBody @Valid OutfitCreationDTO dto) {
    return new ResponseEntity<>(outfitService.create(dto), HttpStatus.CREATED);
  }

  @PostMapping("{id}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Outfit> update(@RequestBody @Valid Outfit outfit) {
    return new ResponseEntity<>(outfitService.update(outfit), HttpStatus.OK);
  }

  @PostMapping("{id}/tags")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<OutfitTag> addTag(@PathVariable("id") Integer id, @RequestBody String tag) {
    return new ResponseEntity<>(outfitService.addTag(id, tag), HttpStatus.CREATED);
  }

  @PostMapping("{id}/products")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<Product> addProduct(@PathVariable("id") Integer id, @RequestBody OutfitCreationProductDTO dto) {
    return new ResponseEntity<>(outfitService.addProduct(id, dto), HttpStatus.CREATED);
  }

  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<String> delete(@PathVariable("id") Integer id) {
    return new ResponseEntity<>("Outfit successfully deleted.", HttpStatus.OK);
  }
}
