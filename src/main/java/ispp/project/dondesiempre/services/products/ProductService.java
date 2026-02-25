package ispp.project.dondesiempre.services.products;

import ispp.project.dondesiempre.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.models.products.dto.ProductCreationDTO;
import ispp.project.dondesiempre.repositories.products.ProductRepository;
import ispp.project.dondesiempre.repositories.stores.StoreRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

  public Product getProductById(Integer id) {
    Product product =
        productRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    return product;
  }

  public List<Product> getAllProducts() {
    return productRepository.findAll();
  }

  public List<Product> getAllDiscountedProducts() {
    return productRepository.findAllDiscountedProducts();
  }

  @Transactional
  public Product updateProductDiscount(Integer id, Integer discountedPriceInCents) {
    if (discountedPriceInCents > getProductById(id).getPriceInCents()) {
      throw new InvalidRequestException("Discounted price cannot be greater than original price");
    }
    Product product = getProductById(id);
    product.setDiscountedPriceInCents(discountedPriceInCents);
    Product updatedProduct = productRepository.save(product);
    return updatedProduct;
  }
}
