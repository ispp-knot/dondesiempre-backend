package ispp.project.dondesiempre.controllers;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import ispp.project.dondesiempre.models.outfits.Outfit;
import ispp.project.dondesiempre.models.outfits.dto.OutfitCreationDTO;
import ispp.project.dondesiempre.services.OutfitService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/outfits")
public class OutfitController {
  private OutfitService outfitService;

  public OutfitController(OutfitService outfitService) {
    this.outfitService = outfitService;
  }

  @GetMapping("{id}")
  public ResponseEntity<Outfit> getById(@PathVariable("id") Integer id) {
    return new ResponseEntity<>(outfitService.findById(id), HttpStatus.FOUND);
  }

  @GetMapping("store/{storeId}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<Outfit>> getByStoreId(@PathVariable("storeId") Integer storeId) {
    return new ResponseEntity<>(outfitService.findByStore(storeId), HttpStatus.FOUND);
  }

  @PostMapping("")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<Outfit> create(@RequestBody @Valid OutfitCreationDTO dto) {
    return new ResponseEntity<>(outfitService.create(dto), HttpStatus.CREATED);
  }
}
