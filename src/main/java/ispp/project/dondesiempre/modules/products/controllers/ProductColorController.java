package ispp.project.dondesiempre.modules.products.controllers;

import ispp.project.dondesiempre.modules.products.dtos.ProductColorDTO;
import ispp.project.dondesiempre.modules.products.models.ProductColor;
import ispp.project.dondesiempre.modules.products.services.ProductColorService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/product-colors")
@RequiredArgsConstructor
public class ProductColorController {

  private final ProductColorService productColorService;

  @GetMapping
  public ResponseEntity<List<ProductColorDTO>> getAllProductColors() {
    List<ProductColor> colors = productColorService.getAllProductColors();
    List<ProductColorDTO> dtos = colors.stream().map(ProductColorDTO::new).toList();
    return new ResponseEntity<>(dtos, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductColorDTO> getProductColorById(@PathVariable UUID id) {
    ProductColor color = productColorService.getProductColorById(id);
    return new ResponseEntity<>(new ProductColorDTO(color), HttpStatus.OK);
  }
}
