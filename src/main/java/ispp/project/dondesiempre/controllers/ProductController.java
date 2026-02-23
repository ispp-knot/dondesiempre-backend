package ispp.project.dondesiempre.controllers;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.models.products.dto.DiscountModificationDTO;
import ispp.project.dondesiempre.models.products.dto.GetProductDTO;
import ispp.project.dondesiempre.models.products.dto.ProductCreationDTO;
import ispp.project.dondesiempre.services.ProductService;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  @GetMapping("")
  public ResponseEntity<List<GetProductDTO>> getAllProducts() {
    List<Product> products = productService.getAllProducts();
    List<GetProductDTO> dtos = products.stream().map(GetProductDTO::fromProduct).toList();
    return new ResponseEntity<>(dtos, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<GetProductDTO> getProductById(@PathVariable Integer id) {
    Product product = productService.getProductById(id);
    if (product == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    GetProductDTO dto = GetProductDTO.fromProduct(product);
    return new ResponseEntity<>(dto, HttpStatus.OK);
  }

  @GetMapping("/discounted")
  public ResponseEntity<List<GetProductDTO>> getDiscountedProducts() {
    List<Product> products = productService.getAllDiscountedProducts();
    List<GetProductDTO> dtos = products.stream().map(GetProductDTO::fromProduct).toList();
    return new ResponseEntity<>(dtos, HttpStatus.OK);
  }

  @PostMapping("")
  public ResponseEntity<Product> createProduct(@RequestBody @Valid ProductCreationDTO dto) {
    Product savedProduct = productService.saveProduct(dto);
    return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
  }

  @PutMapping("/{id}/discount")
  public ResponseEntity<Product> updateDiscount(
      @PathVariable Integer id, @RequestBody DiscountModificationDTO discount) {
    try {
      Product updatedProduct =
          productService.updateProductDiscount(id, discount.getDiscountedPriceInCents());
      return new ResponseEntity<>(updatedProduct, HttpStatus.ACCEPTED);
    } catch (RuntimeException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }
}
