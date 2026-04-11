package ispp.project.dondesiempre.modules.products.controllers;

import ispp.project.dondesiempre.modules.products.dtos.ProductSizeDTO;
import ispp.project.dondesiempre.modules.products.models.ProductSize;
import ispp.project.dondesiempre.modules.products.services.ProductSizeService;
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
@RequestMapping("/api/v1/product-sizes")
@RequiredArgsConstructor
public class ProductSizeController {

  private final ProductSizeService productSizeService;

  @GetMapping
  public ResponseEntity<List<ProductSizeDTO>> getAllProductSizes() {
    List<ProductSize> sizes = productSizeService.getAllProductSizes();
    List<ProductSizeDTO> dtos = sizes.stream().map(ProductSizeDTO::new).toList();
    return new ResponseEntity<>(dtos, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductSizeDTO> getProductSizeById(@PathVariable UUID id) {
    ProductSize size = productSizeService.getProductSizeById(id);
    return new ResponseEntity<>(new ProductSizeDTO(size), HttpStatus.OK);
  }
}
