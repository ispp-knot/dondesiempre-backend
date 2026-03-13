package ispp.project.dondesiempre.modules.products.controllers;

import ispp.project.dondesiempre.modules.products.dtos.DiscountModificationDTO;
import ispp.project.dondesiempre.modules.products.dtos.ProductCreationDTO;
import ispp.project.dondesiempre.modules.products.dtos.ProductDTO;
import ispp.project.dondesiempre.modules.products.models.Product;
import ispp.project.dondesiempre.modules.products.services.ProductService;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.services.StoreService;
import ispp.project.dondesiempre.modules.stores.services.StorefrontService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductController {

  private final StoreService storeService;
  private final ProductService productService;
  private final StorefrontService storefrontService;

  @GetMapping("products")
  public ResponseEntity<List<ProductDTO>> getAllProducts() {
    List<Product> products = productService.getAllProducts();
    List<ProductDTO> dtos = products.stream().map(ProductDTO::new).toList();
    return new ResponseEntity<>(dtos, HttpStatus.OK);
  }

  @GetMapping("products/{id}")
  public ResponseEntity<ProductDTO> getProductById(@PathVariable UUID id) {

    Product product = productService.getProductById(id);
    ProductDTO dto = new ProductDTO(product);
    return new ResponseEntity<>(dto, HttpStatus.OK);
  }

  @GetMapping("products/discounted")
  public ResponseEntity<List<ProductDTO>> getDiscountedProducts() {
    List<Product> products = productService.getAllDiscountedProducts();
    List<ProductDTO> dtos = products.stream().map(ProductDTO::new).toList();
    return new ResponseEntity<>(dtos, HttpStatus.OK);
  }

  @PostMapping(value = "products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Product> createProduct(
      @RequestPart("dto") @Valid ProductCreationDTO dto,
      @RequestPart(value = "image", required = false) MultipartFile image,
      @RequestParam UUID storeId) {
    Product savedProduct = productService.saveProduct(dto, image, storeId);
    return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
  }

  @PutMapping("products/{id}/discount")
  public ResponseEntity<Product> updateDiscount(
      @PathVariable UUID id, @RequestBody DiscountModificationDTO discount) {
    Product updatedProduct =
        productService.updateProductDiscount(id, discount.getDiscountedPriceInCents());
    return new ResponseEntity<>(updatedProduct, HttpStatus.ACCEPTED);
  }

  @GetMapping("/stores/{storeId}/products")
  public ResponseEntity<List<ProductDTO>> getByStoreId(@PathVariable UUID storeId) {
    Store store = storeService.findById(storeId);
    List<ProductDTO> dtos =
        productService.findByStore(store).stream().map(ProductDTO::new).toList();
    return new ResponseEntity<>(dtos, HttpStatus.OK);
  }
}
