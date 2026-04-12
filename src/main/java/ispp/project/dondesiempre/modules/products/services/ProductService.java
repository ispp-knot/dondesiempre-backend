package ispp.project.dondesiempre.modules.products.services;

import ispp.project.dondesiempre.modules.auth.services.AuthService;
import ispp.project.dondesiempre.modules.common.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.common.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.modules.outfits.repositories.OutfitProductRepository;
import ispp.project.dondesiempre.modules.products.dtos.ProductCreationDTO;
import ispp.project.dondesiempre.modules.products.dtos.ProductUpdateDTO;
import ispp.project.dondesiempre.modules.products.models.Product;
import ispp.project.dondesiempre.modules.products.repositories.ProductRepository;
import ispp.project.dondesiempre.modules.promotions.repositories.PromotionProductRepository;
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
  private final OutfitProductRepository outfitProductRepository;
  private final PromotionProductRepository promotionProductRepository;

  private void validateMinPriceInCents(Integer priceInCents) throws InvalidRequestException {
    if (priceInCents != null && priceInCents < 1) {
      throw new InvalidRequestException("Price must be at least 1 cent.");
    }
  }

  @Transactional(
      rollbackFor = {
        UnauthorizedException.class,
        ResourceNotFoundException.class,
        InvalidRequestException.class
      })
  public Product createProduct(ProductCreationDTO dto, MultipartFile image, UUID storeId)
      throws UnauthorizedException, ResourceNotFoundException, InvalidRequestException {
    validateMinPriceInCents(dto.getPriceInCents());
    Store store =
        storeRepository
            .findById(storeId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Store with ID " + storeId + " not found."));
    authService.assertUserOwnsStore(store);
    Product product = new Product();
    product.setName(dto.getName());
    product.setPriceInCents(dto.getPriceInCents());
    product.setDiscountPercentage(null);
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
            .findByIdAndIsDeletedIsFalse(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    return product;
  }

  @Transactional(readOnly = true)
  public List<Product> findProductsByPromotionId(UUID promotionId) {
    return productRepository.findProductsByPromotionId(promotionId);
  }

  @Transactional(readOnly = true)
  public List<Product> getOutfitProductsById(UUID outfitId) {
    return productRepository.findOutfitProductsByOutfitId(outfitId);
  }

  @Transactional(readOnly = true)
  public List<Product> getAllDiscountedProducts() {
    return productRepository.findByDiscountPercentageIsNotNullAndIsDeletedIsFalse();
  }

  @Transactional(rollbackFor = ResourceNotFoundException.class)
  public Product updateProductDiscount(UUID id, Integer discountPercentage)
      throws ResourceNotFoundException {

    Product product = applicationContext.getBean(ProductService.class).getProductById(id);
    authService.assertUserOwnsStore(product.getStore());

    if (discountPercentage != 0) product.setDiscountPercentage(discountPercentage);
    else product.setDiscountPercentage(null);
    return productRepository.save(product);
  }

  @Transactional
  public List<Product> findByStoreId(UUID storeId) {
    return productRepository.findByStoreIdAndIsDeletedIsFalse(storeId);
  }

  @Transactional(
      rollbackFor = {
        UnauthorizedException.class,
        ResourceNotFoundException.class,
        InvalidRequestException.class
      })
  public Product updateProduct(UUID productId, ProductUpdateDTO dto, MultipartFile image)
      throws UnauthorizedException, ResourceNotFoundException, InvalidRequestException {
    validateMinPriceInCents(dto.getPriceInCents());
    Product product =
        productRepository
            .findByIdAndIsDeletedIsFalse(productId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException("Product with ID " + productId + " not found."));
    authService.assertUserOwnsStore(product.getStore());

    if (dto != null) {
      if (dto.getName() != null) {
        product.setName(dto.getName());
      }
      if (dto.getPriceInCents() != null) {
        product.setPriceInCents(dto.getPriceInCents());
      }
      if (dto.getDescription() != null) {
        product.setDescription(dto.getDescription());
      }
      if (dto.getProductTypeId() != null) {
        product.setType(productTypeService.getProductTypeById(dto.getProductTypeId()));
      }
    }

    if (image != null) {
      product.setImage(cloudinaryService.upload(image));
    }

    return productRepository.save(product);
  }

  @Transactional(rollbackFor = {UnauthorizedException.class, ResourceNotFoundException.class})
  public void deleteProduct(UUID productId)
      throws UnauthorizedException, ResourceNotFoundException {
    boolean outfitsUsingProduct = outfitProductRepository.existsByProductId(productId);
    boolean promotionUsingProduct = promotionProductRepository.existsByProductId(productId);

    if (outfitsUsingProduct) {
      throw new InvalidRequestException(
          "Cannot delete product because it is used in one or more outfits.");
    }

    if (promotionUsingProduct) {
      throw new InvalidRequestException(
          "Cannot delete product because it is used in one or more promotions.");
    }

    Product product =
        productRepository
            .findByIdAndIsDeletedIsFalse(productId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException("Product with ID " + productId + " not found."));
    authService.assertUserOwnsStore(product.getStore());

    product.setDeleted(true);
    productRepository.save(product);
  }

  public List<Product> findAll() {
    return productRepository.findByIsDeletedIsFalse();
  }
}
