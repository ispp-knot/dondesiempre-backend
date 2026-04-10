package ispp.project.dondesiempre.modules.products.controllers;

import ispp.project.dondesiempre.modules.products.dtos.ProductVariantCreationDTO;
import ispp.project.dondesiempre.modules.products.dtos.ProductVariantDTO;
import ispp.project.dondesiempre.modules.products.dtos.ProductVariantUpdateDTO;
import ispp.project.dondesiempre.modules.products.models.ProductVariant;
import ispp.project.dondesiempre.modules.products.services.ProductVariantService;
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
@RequestMapping("/api/v1/product-variants")
@RequiredArgsConstructor
public class ProductVariantController {

  private final ProductVariantService productVariantService;

  @GetMapping
  public ResponseEntity<List<ProductVariantDTO>> getAllProductVariants() {
    List<ProductVariant> variants = productVariantService.getAllProductVariants();
    List<ProductVariantDTO> dtos = variants.stream().map(ProductVariantDTO::new).toList();
    return new ResponseEntity<>(dtos, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductVariantDTO> getProductVariantById(@PathVariable UUID id) {
    ProductVariant variant = productVariantService.getProductVariantById(id);
    return new ResponseEntity<>(new ProductVariantDTO(variant), HttpStatus.OK);
  }

  @GetMapping("/product/{productId}")
  public ResponseEntity<List<ProductVariantDTO>> getVariantsByProductId(
      @PathVariable UUID productId) {
    List<ProductVariant> variants = productVariantService.getVariantsByProductId(productId);
    List<ProductVariantDTO> dtos = variants.stream().map(ProductVariantDTO::new).toList();
    return new ResponseEntity<>(dtos, HttpStatus.OK);
  }

  @GetMapping("/product/{productId}/available")
  public ResponseEntity<List<ProductVariantDTO>> getAvailableVariantsByProductId(
      @PathVariable UUID productId) {
    List<ProductVariant> variants =
        productVariantService.getAvailableVariantsByProductId(productId);
    List<ProductVariantDTO> dtos = variants.stream().map(ProductVariantDTO::new).toList();
    return new ResponseEntity<>(dtos, HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<ProductVariantDTO> createProductVariant(
      @RequestBody @Valid ProductVariantCreationDTO dto) {
    ProductVariant savedVariant = productVariantService.createProductVariant(dto);
    return new ResponseEntity<>(new ProductVariantDTO(savedVariant), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProductVariantDTO> updateProductVariant(
      @PathVariable UUID id, @RequestBody @Valid ProductVariantUpdateDTO dto) {
    ProductVariant updatedVariant = productVariantService.updateProductVariant(id, dto);
    return new ResponseEntity<>(new ProductVariantDTO(updatedVariant), HttpStatus.ACCEPTED);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProductVariant(@PathVariable UUID id) {
    productVariantService.deleteProductVariant(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
