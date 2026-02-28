package ispp.project.dondesiempre.controllers;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.models.products.dto.DiscountModificationDTO;
import ispp.project.dondesiempre.models.products.dto.ProductCreationDTO;
import ispp.project.dondesiempre.models.products.dto.ProductDTO;
import ispp.project.dondesiempre.models.storefronts.Storefront;
import ispp.project.dondesiempre.services.products.ProductService;
import ispp.project.dondesiempre.services.storefronts.StorefrontService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;
  private final StorefrontService storefrontService;

  @GetMapping("products")
  public ResponseEntity<List<ProductDTO>> getAllProducts() {
    List<Product> products = productService.getAllProducts();
    List<ProductDTO> dtos = products.stream().map(ProductDTO::fromProduct).toList();
    return new ResponseEntity<>(dtos, HttpStatus.OK);
  }

  @GetMapping("products/{id}")
  public ResponseEntity<ProductDTO> getProductById(@PathVariable UUID id) {

    Product product = productService.getProductById(id);
    ProductDTO dto = ProductDTO.fromProduct(product);
    return new ResponseEntity<>(dto, HttpStatus.OK);
  }

  @GetMapping("products/discounted")
  public ResponseEntity<List<ProductDTO>> getDiscountedProducts() {
    List<Product> products = productService.getAllDiscountedProducts();
    List<ProductDTO> dtos = products.stream().map(ProductDTO::fromProduct).toList();
    return new ResponseEntity<>(dtos, HttpStatus.OK);
  }

  @PostMapping("products")
  public ResponseEntity<Product> createProduct(@RequestBody @Valid ProductCreationDTO dto) {
    Product savedProduct = productService.saveProduct(dto);
    return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
  }

  @PutMapping("products/{id}/discount")
  public ResponseEntity<Product> updateDiscount(
      @PathVariable UUID id, @RequestBody DiscountModificationDTO discount) {
    Product updatedProduct =
        productService.updateProductDiscount(id, discount.getDiscountedPriceInCents());
    return new ResponseEntity<>(updatedProduct, HttpStatus.ACCEPTED);
  }

  @GetMapping("/storefronts/{storefrontId}/products")
  public ResponseEntity<List<ProductDTO>> getByStorefrontId(@PathVariable UUID storefrontId) {
    Storefront storefront;

    storefront = storefrontService.findById(storefrontId);
    return new ResponseEntity<>(productService.findByStorefront(storefront), HttpStatus.OK);
  }
}
