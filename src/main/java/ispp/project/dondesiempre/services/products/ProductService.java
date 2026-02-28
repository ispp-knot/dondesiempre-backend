package ispp.project.dondesiempre.services.products;

import ispp.project.dondesiempre.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.models.products.dto.ProductCreationDTO;
import ispp.project.dondesiempre.repositories.products.ProductRepository;
import ispp.project.dondesiempre.repositories.stores.StoreRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final StoreRepository storeRepository;
  private final ProductTypeService productTypeService;

  @Transactional
  public Product saveProduct(ProductCreationDTO dto) {
    if (dto.getDiscountedPriceInCents() > dto.getPriceInCents()) {
      throw new InvalidRequestException("Discounted price cannot be greater than original price");
    }
    Product product = new Product();
    product.setName(dto.getName());
    product.setPriceInCents(dto.getPriceInCents());
    product.setDiscountedPriceInCents(dto.getDiscountedPriceInCents());
    product.setDescription(dto.getDescription());
    product.setType(productTypeService.getProductTypeById(dto.getTypeId()));
    product.setStore(storeRepository.getReferenceById(dto.getStoreId()));
    return productRepository.save(product);
  }

  @Transactional(readOnly = true)
  public Product getProductById(UUID id) {
    Product product =
        productRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    return product;
  }

  @Transactional(readOnly = true)
  public List<Product> getAllProducts() {
    return productRepository.findAll();
  }

  @Transactional(readOnly = true)
  public List<Product> getAllDiscountedProducts() {
    return productRepository.findAllDiscountedProducts();
  }

  @Transactional
  public Product updateProductDiscount(UUID id, Integer discountedPriceInCents) {
    if (discountedPriceInCents > getProductById(id).getPriceInCents()) {
      throw new InvalidRequestException("Discounted price cannot be greater than original price");
    }
    Product product = getProductById(id);
    product.setDiscountedPriceInCents(discountedPriceInCents);
    Product updatedProduct = productRepository.save(product);
    return updatedProduct;
  }
}
