package ispp.project.dondesiempre.modules.promotions.services;

import ispp.project.dondesiempre.modules.auth.services.AuthService;
import ispp.project.dondesiempre.modules.common.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.common.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.modules.products.dtos.ProductDTO;
import ispp.project.dondesiempre.modules.products.models.Product;
import ispp.project.dondesiempre.modules.products.services.ProductService;
import ispp.project.dondesiempre.modules.promotions.dtos.PromotionCreationDTO;
import ispp.project.dondesiempre.modules.promotions.dtos.PromotionUpdateDTO;
import ispp.project.dondesiempre.modules.promotions.models.Promotion;
import ispp.project.dondesiempre.modules.promotions.models.PromotionProduct;
import ispp.project.dondesiempre.modules.promotions.repositories.PromotionRepository;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.services.StoreService;
import ispp.project.dondesiempre.utils.cloudinary.CloudinaryService;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PromotionService {

  private final ApplicationContext applicationContext;
  private final PromotionRepository promotionRepository;
  private final AuthService authService;
  private final StoreService storeService;
  private final ProductService productService;
  private final PromotionProductService promotionProductService;
  private final CloudinaryService cloudinaryService;

  @Transactional(
      rollbackFor = {
        UnauthorizedException.class,
        ResourceNotFoundException.class,
        InvalidRequestException.class
      })
  public Promotion createPromotion(PromotionCreationDTO dto, MultipartFile image)
      throws UnauthorizedException, ResourceNotFoundException, InvalidRequestException {
    Promotion promotion = new Promotion();
    promotion.setName(dto.getName());
    if (dto.getDiscountPercentage() < 1 || dto.getDiscountPercentage() > 100) {
      throw new InvalidRequestException("Discount must be between 1 and 100");
    }
    promotion.setActive(dto.isActive());
    promotion.setDiscountPercentage(dto.getDiscountPercentage());
    promotion.setDescription(dto.getDescription());

    if (image != null) {
      promotion.setPromotionImageUrl(cloudinaryService.upload(image));
    }

    if (dto.getStartDate() != null) {
      promotion.setStartDate(dto.getStartDate());
    }

    if (dto.getEndDate() != null) {
      promotion.setEndDate(dto.getEndDate());
    }

    Store store = storeService.findById(dto.getStoreId());
    promotion.setStore(store);

    authService.assertUserOwnsStore(store);

    Set<Product> products =
        dto.getProductIds().stream()
            .map(productService::getProductById)
            .collect(java.util.stream.Collectors.toSet());

    if (products.stream()
        .anyMatch(product -> !product.getStore().getId().equals(dto.getStoreId()))) {
      throw new InvalidRequestException(
          "All products must belong to the same store as the promotion");
    }

    try {
      Promotion saved = promotionRepository.save(promotion);
      products.forEach(product -> addProduct(saved.getId(), product.getId()));
      return saved;
    } catch (Exception e) {
      throw new InvalidRequestException("Error creating promotion");
    }
  }

  @Transactional(
      rollbackFor = {
        UnauthorizedException.class,
        ResourceNotFoundException.class,
        InvalidRequestException.class
      })
  public PromotionProduct addProduct(UUID promotionId, UUID productId)
      throws UnauthorizedException, ResourceNotFoundException, InvalidRequestException {
    Promotion promotion =
        applicationContext.getBean(PromotionService.class).getPromotionById(promotionId);
    Product product = productService.getProductById(productId);
    PromotionProduct promotionProduct = new PromotionProduct();
    promotionProduct.setPromotion(promotion);
    promotionProduct.setProduct(product);
    authService.assertUserOwnsStore(product.getStore());
    authService.assertUserOwnsStore(promotion.getStore());

    try {
      return promotionProductService.save(promotionProduct);
    } catch (Exception e) {
      throw new InvalidRequestException(
          "This product is already part of the promotion or there was an error adding it.");
    }
  }

  @Transactional(readOnly = true, rollbackFor = ResourceNotFoundException.class)
  public Promotion getPromotionById(UUID id) throws ResourceNotFoundException {
    return promotionRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with id: " + id));
  }

  @Transactional(readOnly = true)
  public List<Promotion> getAllPromotions() {
    return promotionRepository.findAll();
  }

  public Integer calculateDiscountedPrice(Integer originalPrice, Integer discountPercentage)
      throws InvalidRequestException {
    if (originalPrice == null || discountPercentage == null) {
      throw new InvalidRequestException("Price and discount must not be null");
    }

    if (discountPercentage < 1 || discountPercentage > 100) {
      throw new InvalidRequestException("Discount must be between 1 and 100");
    }

    double discounted = originalPrice * (1 - discountPercentage / 100.0);

    return (int) Math.round(discounted);
  }

  @Transactional(
      rollbackFor = {
        UnauthorizedException.class,
        ResourceNotFoundException.class,
        InvalidRequestException.class
      })
  public Promotion updatePromotion(UUID id, PromotionUpdateDTO dto, MultipartFile image)
      throws UnauthorizedException, ResourceNotFoundException, InvalidRequestException {

    Promotion promotion = applicationContext.getBean(PromotionService.class).getPromotionById(id);
    authService.assertUserOwnsStore(promotion.getStore());

    if (dto.getName() != null) {
      promotion.setName(dto.getName());
    }
    if (dto.getDescription() != null) {
      promotion.setDescription(dto.getDescription());
    }
    if (dto.isActive() != promotion.isActive()) {
      promotion.setActive(dto.isActive());
    }
    if (dto.getDiscountPercentage() != null) {
      if (dto.getDiscountPercentage() < 1 || dto.getDiscountPercentage() > 100) {
        throw new InvalidRequestException("Discount must be between 1 and 100");
      }
      promotion.setDiscountPercentage(dto.getDiscountPercentage());
    }

    if (image != null) {
      promotion.setPromotionImageUrl(cloudinaryService.upload(image));
    }

    if (dto.getStartDate() != null) {
      promotion.setStartDate(dto.getStartDate());
    }

    if (dto.getEndDate() != null) {
      promotion.setEndDate(dto.getEndDate());
    }

    if (dto.getProductIds() != null && !dto.getProductIds().isEmpty()) {
      Set<Product> products =
          dto.getProductIds().stream()
              .map(productService::getProductById)
              .collect(Collectors.toSet());

      if (products.stream()
          .anyMatch(product -> !product.getStore().getId().equals(promotion.getStore().getId()))) {
        throw new InvalidRequestException(
            "All products must belong to the same store as the promotion");
      }

      // Remove existing associations
      promotionProductService.findByPromotionId(id).forEach(promotionProductService::delete);

      // Forzar flush para que el DELETE llegue a BD antes del INSERT
      promotionProductService.flushChanges(); //
      // Add new associations
      products.forEach(
          product ->
              applicationContext.getBean(PromotionService.class).addProduct(id, product.getId()));
    }
    try {
      return promotionRepository.save(promotion);
    } catch (Exception e) {
      throw new InvalidRequestException("Error updating promotion");
    }
  }

  @Transactional(rollbackFor = {UnauthorizedException.class, ResourceNotFoundException.class})
  public void deletePromotion(UUID id) throws UnauthorizedException, ResourceNotFoundException {
    Promotion promotion = applicationContext.getBean(PromotionService.class).getPromotionById(id);
    authService.assertUserOwnsStore(promotion.getStore());
    promotionProductService.findByPromotionId(id).forEach(promotionProductService::delete);
    promotionRepository.delete(promotion);
  }

  @Transactional(readOnly = true, rollbackFor = ResourceNotFoundException.class)
  public List<ProductDTO> getAllProductsDTOByPromotionId(UUID promotionId)
      throws ResourceNotFoundException {
    applicationContext
        .getBean(PromotionService.class)
        .getPromotionById(promotionId); // Ensure promotion exists
    List<Product> products = productService.findProductsByPromotionId(promotionId);
    return products.stream().map(ProductDTO::new).collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<Promotion> getPromotionsByStoreId(UUID storeId) {
    return promotionRepository.findByStoreId(storeId);
  }

  @Transactional(readOnly = true)
  public List<Promotion> getPromotionsByProductId(UUID productId) {
    return promotionRepository.findPromotionsByProductId(productId);
  }
}
