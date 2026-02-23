package ispp.project.dondesiempre.services;

import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.models.products.dto.ProductCreationDTO;
import ispp.project.dondesiempre.repositories.products.ProductRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final ProductTypeService productTypeService;

  @Transactional
  public Product saveProduct(ProductCreationDTO dto) {
    Product product = new Product();
    product.setName(dto.getName());
    product.setPriceEuros(dto.getPriceEuros());
    product.setPriceCents(dto.getPriceCents());
    product.setDiscountedPriceEuros(dto.getDiscountEuros());
    product.setDiscountedPriceCents(dto.getDiscountCents());
    product.setDescription(dto.getDescription());
    product.setType(productTypeService.getProductTypeById(dto.getTypeId()));
    return productRepository.save(product);
  }

  public Product getProductById(Integer id) {
    Product product =
        productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    return product;
  }

  public List<Product> getAllProducts() {
    return productRepository.findAll();
  }

  public List<Product> getAllDiscountedProducts() {
    return productRepository.findAllDiscountedProducts();
  }

  @Transactional
  public Product updateProductDiscount(
      Integer id, Integer discountedEuros, Integer discountedCents) {
    Product product = getProductById(id);
    product.setDiscountedPriceEuros(discountedEuros);
    product.setDiscountedPriceCents(discountedCents);
    Product updatedProduct = productRepository.save(product);
    return updatedProduct;
  }
}
