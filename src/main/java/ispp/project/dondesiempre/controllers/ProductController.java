package ispp.project.dondesiempre.controllers;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.models.products.dto.ProductCreationDTO;
import ispp.project.dondesiempre.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<Product> getProductById(Integer id) {
    Product product = productService.getProductById(id);
    if (product == null) {
      return ResponseEntity.notFound().build();
    }
    return new ResponseEntity<>(product, HttpStatus.FOUND);
  }

  @GetMapping("/{id}/real-price")
  public ResponseEntity<Double> getRealPrice(Integer id) {
    Product product = productService.getProductById(id);
    if (product == null) {
      return ResponseEntity.notFound().build();
    }
    Double realPrice = productService.getRealPrice(product);
    return new ResponseEntity<>(realPrice, HttpStatus.OK);
  }

  @PostMapping("")
  public ResponseEntity<?> createProduct(@RequestBody @Valid ProductCreationDTO dto) {
    Product savedProduct = productService.saveProduct(dto);
    return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
  }
}
