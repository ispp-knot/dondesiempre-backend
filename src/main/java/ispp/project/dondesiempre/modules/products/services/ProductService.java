package ispp.project.dondesiempre.modules.products.services;

import ispp.project.dondesiempre.modules.auth.services.AuthService;
import ispp.project.dondesiempre.modules.common.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.common.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.modules.products.dtos.ProductCreationDTO;
import ispp.project.dondesiempre.modules.products.models.Product;
import ispp.project.dondesiempre.modules.products.repositories.ProductRepository;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.repositories.StoreRepository;
import ispp.project.dondesiempre.utils.cloudinary.CloudinaryService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ApplicationContext applicationContext;
  private final ProductRepository productRepository;
  private final StoreRepository storeRepository;
  private final ProductTypeService productTypeService;
  private final AuthService authService;
  private final CloudinaryService cloudinaryService;

  @Transactional(
      rollbackFor = {
        UnauthorizedException.class,
        ResourceNotFoundException.class,
        InvalidRequestException.class
      })
  public Product saveProduct(ProductCreationDTO dto, MultipartFile image, UUID storeId)
      throws UnauthorizedException, ResourceNotFoundException, InvalidRequestException {
    if (dto.getDiscountedPriceInCents() > dto.getPriceInCents()) {
      throw new InvalidRequestException("Discounted price cannot be greater than original price");
    }
    Store store =
        storeRepository
            .findById(storeId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Store with ID " + storeId + " not found."));
    authService.assertUserOwnsStore(store);
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

  @Transactional(readOnly = true, rollbackFor = ResourceNotFoundException.class)
  public Product getProductById(UUID id) throws ResourceNotFoundException {
    Product product =
        productRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    return product;
  }

  @Transactional(readOnly = true)
  public List<Product> findProductsByPromotionId(UUID promotionId) {
    return productRepository.findProductsByPromotionId(promotionId);
  }

  @Transactional(readOnly = true)
  public List<Product> getAllProducts() {
    return productRepository.findAll();
  }

  @Transactional(readOnly = true)
  public List<Product> getOutfitProductsById(UUID outfitId) {
    return productRepository.findOutfitProductsByOutfitId(outfitId);
  }

  @Transactional(readOnly = true)
  public List<Product> getAllDiscountedProducts() {
    return productRepository.findAllDiscountedProducts();
  }

  @Transactional(rollbackFor = ResourceNotFoundException.class)
  public Product updateProductDiscount(UUID id, Integer discountedPriceInCents)
      throws ResourceNotFoundException {
    Product product = applicationContext.getBean(ProductService.class).getProductById(id);
    authService.assertUserOwnsStore(product.getStore());
    if (discountedPriceInCents > product.getPriceInCents()) {
      throw new InvalidRequestException("Discounted price cannot be greater than original price");
    }
    product.setDiscountedPriceInCents(discountedPriceInCents);
    return productRepository.save(product);
  }

  @Transactional
  public List<Product> findByStore(Store store) {
    return productRepository.findByStoreId(store.getId());
  }
}
