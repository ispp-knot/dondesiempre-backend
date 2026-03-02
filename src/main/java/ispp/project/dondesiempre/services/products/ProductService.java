package ispp.project.dondesiempre.services.products;

import ispp.project.dondesiempre.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.models.products.dto.ProductCreationDTO;
import ispp.project.dondesiempre.models.storefronts.Storefront;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.repositories.products.ProductRepository;
import ispp.project.dondesiempre.repositories.stores.StoreRepository;
import ispp.project.dondesiempre.services.CloudinaryService;
import ispp.project.dondesiempre.services.UserService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final StoreRepository storeRepository;
  private final ProductTypeService productTypeService;
  private final UserService userService;
  private final CloudinaryService cloudinaryService;

  @Transactional
  public Product saveProduct(ProductCreationDTO dto, MultipartFile image) {
    if (dto.getDiscountedPriceInCents() > dto.getPriceInCents()) {
      throw new InvalidRequestException("Discounted price cannot be greater than original price");
    }
    Store store =
        storeRepository
            .findById(dto.getStoreId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Store with ID " + dto.getStoreId() + " not found."));
    userService.assertUserOwnsStore(store);
    Product product = new Product();
    product.setName(dto.getName());
    product.setPriceInCents(dto.getPriceInCents());
    product.setDiscountedPriceInCents(dto.getDiscountedPriceInCents());
    product.setDescription(dto.getDescription());
    if (image != null && !image.isEmpty()) {
      product.setImage(cloudinaryService.upload(image));
    }
    product.setType(productTypeService.getProductTypeById(dto.getTypeId()));
    product.setStore(store);
    return productRepository.save(product);
  }

  public Product getProductById(UUID id) {
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
  public Product updateProductDiscount(UUID id, Integer discountedPriceInCents) {
    Product product = getProductById(id);
    userService.assertUserOwnsStore(product.getStore());
    if (discountedPriceInCents > product.getPriceInCents()) {
      throw new InvalidRequestException("Discounted price cannot be greater than original price");
    }
    product.setDiscountedPriceInCents(discountedPriceInCents);
    return productRepository.save(product);
  }

  @Transactional
  public List<Product> findByStorefront(Storefront storefront) {
    return productRepository.findByStorefrontId(storefront.getId());
  }
}
