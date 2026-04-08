package ispp.project.dondesiempre.modules.products.controllers;

import ispp.project.dondesiempre.modules.common.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.modules.outfits.models.Outfit;
import ispp.project.dondesiempre.modules.outfits.services.OutfitService;
import ispp.project.dondesiempre.modules.products.dtos.ProductCreationDTO;
import ispp.project.dondesiempre.modules.products.dtos.ProductDTO;
import ispp.project.dondesiempre.modules.products.dtos.ProductDiscountUpdateDTO;
import ispp.project.dondesiempre.modules.products.dtos.ProductUpdateDTO;
import ispp.project.dondesiempre.modules.products.models.Product;
import ispp.project.dondesiempre.modules.products.services.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

  private final ProductService productService;
  private final OutfitService outfitService;

  @GetMapping("products")
  public ResponseEntity<List<ProductDTO>> getAllProducts() {
    List<Product> products = productService.findAll();
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
  public ResponseEntity<ProductDTO> createProduct(
      @RequestPart("dto") @Valid ProductCreationDTO dto,
      @RequestPart(value = "image", required = false) MultipartFile image,
      @RequestParam UUID storeId) {
    Product savedProduct = productService.createProduct(dto, image, storeId);
    return new ResponseEntity<>(new ProductDTO(savedProduct), HttpStatus.CREATED);
  }

  @PatchMapping("products/{id}/discount")
  public ResponseEntity<ProductDTO> updateDiscount(
      @PathVariable UUID id, @RequestBody @Valid ProductDiscountUpdateDTO discount) {
    Product updatedProduct =
        productService.updateProductDiscount(id, discount.getDiscountPercentage());
    return new ResponseEntity<>(new ProductDTO(updatedProduct), HttpStatus.ACCEPTED);
  }

  @GetMapping("/stores/{storeId}/products")
  public ResponseEntity<List<ProductDTO>> getByStoreId(@PathVariable UUID storeId) {
    List<ProductDTO> dtos =
        productService.findByStoreId(storeId).stream().map(ProductDTO::new).toList();
    return new ResponseEntity<>(dtos, HttpStatus.OK);
  }

  @PutMapping(value = "/products/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ProductDTO> updateProduct(
      @PathVariable UUID productId,
      @Valid @RequestPart(value = "product", required = false) ProductUpdateDTO product,
      @RequestPart(value = "image", required = false) MultipartFile image) {
    Product updatedProduct = productService.updateProduct(productId, product, image);
    return new ResponseEntity<>(new ProductDTO(updatedProduct), HttpStatus.ACCEPTED);
  }

  @DeleteMapping("/products/{productId}")
  public void deleteProduct(@PathVariable UUID productId) {
    List<Outfit> outfitsUsingProduct = outfitService.findOutfitsByProductId(productId);
    if (!outfitsUsingProduct.isEmpty()) {
      throw new InvalidRequestException(
          "Cannot delete product because it is used in one or more outfits.");
    }
    productService.deleteProduct(productId);
  }
}
