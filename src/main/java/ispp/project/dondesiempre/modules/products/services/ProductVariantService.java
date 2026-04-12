package ispp.project.dondesiempre.modules.products.services;

import ispp.project.dondesiempre.modules.auth.services.AuthService;
import ispp.project.dondesiempre.modules.common.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.common.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.modules.products.dtos.ProductVariantCreationDTO;
import ispp.project.dondesiempre.modules.products.dtos.ProductVariantUpdateDTO;
import ispp.project.dondesiempre.modules.products.models.Product;
import ispp.project.dondesiempre.modules.products.models.ProductColor;
import ispp.project.dondesiempre.modules.products.models.ProductSize;
import ispp.project.dondesiempre.modules.products.models.ProductVariant;
import ispp.project.dondesiempre.modules.products.repositories.ProductColorRepository;
import ispp.project.dondesiempre.modules.products.repositories.ProductRepository;
import ispp.project.dondesiempre.modules.products.repositories.ProductSizeRepository;
import ispp.project.dondesiempre.modules.products.repositories.ProductVariantRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductVariantService {

  private final ApplicationContext applicationContext;
  private final ProductVariantRepository productVariantRepository;
  private final ProductRepository productRepository;
  private final ProductSizeRepository productSizeRepository;
  private final ProductColorRepository productColorRepository;
  private final AuthService authService;

  private void checkProductVariantExists(UUID productId, UUID sizeId, UUID colorId) {
    if (productVariantRepository.existsByProductIdAndSizeIdAndColorId(productId, sizeId, colorId)) {
      throw new InvalidRequestException("This product variant already exists.");
    }
  }

  @Transactional(
      rollbackFor = {
        UnauthorizedException.class,
        ResourceNotFoundException.class,
      })
  public ProductVariant createProductVariant(ProductVariantCreationDTO dto)
      throws UnauthorizedException, ResourceNotFoundException, InvalidRequestException {
    checkProductVariantExists(dto.getProductId(), dto.getSizeId(), dto.getColorId());
    Product product =
        productRepository
            .findByIdAndIsDeletedIsFalse(dto.getProductId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Product with ID " + dto.getProductId() + " not found."));

    authService.assertUserOwnsStore(product.getStore());

    ProductSize size =
        productSizeRepository
            .findById(dto.getSizeId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "ProductSize with ID " + dto.getSizeId() + " not found."));

    ProductColor color =
        productColorRepository
            .findById(dto.getColorId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "ProductColor with ID " + dto.getColorId() + " not found."));

    ProductVariant variant = new ProductVariant();
    variant.setProduct(product);
    variant.setSize(size);
    variant.setColor(color);
    variant.setIsAvailable(dto.getIsAvailable());

    return productVariantRepository.save(variant);
  }

  @Transactional(readOnly = true, rollbackFor = ResourceNotFoundException.class)
  public ProductVariant getProductVariantById(UUID id) throws ResourceNotFoundException {
    return productVariantRepository
        .findById(id)
        .orElseThrow(
            () -> new ResourceNotFoundException("ProductVariant not found with id: " + id));
  }

  @Transactional(readOnly = true)
  public List<ProductVariant> getAllProductVariants() {
    return productVariantRepository.findAll();
  }

  @Transactional(readOnly = true)
  public List<ProductVariant> getVariantsByProductId(UUID productId) {
    return productVariantRepository.findByProductId(productId);
  }

  @Transactional(readOnly = true)
  public List<ProductVariant> getAvailableVariantsByProductId(UUID productId) {
    return productVariantRepository.findByProductIdAndIsAvailableTrue(productId);
  }

  @Transactional(rollbackFor = {UnauthorizedException.class, ResourceNotFoundException.class})
  public ProductVariant updateProductVariant(UUID id, ProductVariantUpdateDTO dto)
      throws UnauthorizedException, ResourceNotFoundException {
    ProductVariant variant =
        applicationContext.getBean(ProductVariantService.class).getProductVariantById(id);

    authService.assertUserOwnsStore(variant.getProduct().getStore());

    if (dto.getIsAvailable() != null) {
      variant.setIsAvailable(dto.getIsAvailable());
    }

    return productVariantRepository.save(variant);
  }

  @Transactional(rollbackFor = {UnauthorizedException.class, ResourceNotFoundException.class})
  public void deleteProductVariant(UUID id)
      throws UnauthorizedException, ResourceNotFoundException {
    ProductVariant variant =
        applicationContext.getBean(ProductVariantService.class).getProductVariantById(id);

    authService.assertUserOwnsStore(variant.getProduct().getStore());

    productVariantRepository.deleteById(id);
  }
}
