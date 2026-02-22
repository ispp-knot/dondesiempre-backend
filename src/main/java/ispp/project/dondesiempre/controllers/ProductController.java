package ispp.project.dondesiempre.controllers;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.models.products.dto.ProductCreationDTO;
import ispp.project.dondesiempre.services.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @GetMapping("")
  public ResponseEntity<List<Product>> getAllProducts() {
    List<Product> products = productService.getAllProducts();
    return new ResponseEntity<>(products, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Product> getProductById(Integer id) {
    Product product = productService.getProductById(id);
    if (product == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(product, HttpStatus.OK);
  }

  @GetMapping("/{id}/real-price")
  public ResponseEntity<Double> getRealPrice(Integer id) {
    Product product = productService.getProductById(id);
    if (product == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    Double realPrice = productService.getRealPrice(product);
    return new ResponseEntity<>(realPrice, HttpStatus.OK);
  }

  @GetMapping("/discounted")
  public ResponseEntity<List<Product>> getDiscountedProducts() {
    List<Product> products = productService.getAllDiscountedProducts();
    return new ResponseEntity<>(products, HttpStatus.OK);
  }

  @PostMapping("")
  public ResponseEntity<Product> createProduct(@RequestBody @Valid ProductCreationDTO dto) {
    Product savedProduct = productService.saveProduct(dto);
    return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
  }

  @PutMapping("/{id}/discount")
  public ResponseEntity<Product> updateDiscount(Integer id, @RequestBody Double discount) {
    try {
      Product updatedProduct = productService.updateProductDiscount(id, discount);
      return new ResponseEntity<>(updatedProduct, HttpStatus.ACCEPTED);
    } catch (RuntimeException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }
}
