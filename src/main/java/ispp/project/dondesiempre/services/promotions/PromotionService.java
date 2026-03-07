package ispp.project.dondesiempre.services.promotions;

import ispp.project.dondesiempre.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.models.promotions.Promotion;
import ispp.project.dondesiempre.models.promotions.PromotionProduct;
import ispp.project.dondesiempre.models.promotions.dto.PromotionCreationDTO;
import ispp.project.dondesiempre.models.promotions.dto.PromotionUpdateDTO;
import ispp.project.dondesiempre.modules.auth.services.AuthService;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.repositories.StoreRepository;
import ispp.project.dondesiempre.repositories.products.ProductRepository;
import ispp.project.dondesiempre.repositories.promotions.PromotionProductRepository;
import ispp.project.dondesiempre.repositories.promotions.PromotionRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PromotionService {

  private final PromotionRepository promotionRepository;
  private final ProductRepository productRepository;
  private final PromotionProductRepository promotionProductRepository;
  private final StoreRepository storeRepository;
  private final AuthService authService;

  @Transactional
  public Promotion savePromotion(PromotionCreationDTO dto)
      throws ResourceNotFoundException, InvalidRequestException {
    Promotion promotion = new Promotion();
    promotion.setName(dto.getName());
    if (dto.getDiscountPercentage() < 1 || dto.getDiscountPercentage() > 100) {
      throw new InvalidRequestException("Discount must be between 1 and 100");
    }
    promotion.setActive(dto.isActive());
    promotion.setDiscountPercentage(dto.getDiscountPercentage());
    promotion.setDescription(dto.getDescription());

    Store store =
        storeRepository
            .findById(dto.getStoreId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException("Store not found with id: " + dto.getStoreId()));
    promotion.setStore(store);

    authService.assertUserOwnsStore(store);

    Set<Product> products =
        dto.getProductIds().stream()
            .map(
                productId ->
                    productRepository
                        .findById(productId)
                        .orElseThrow(
                            () ->
                                new ResourceNotFoundException(
                                    "Product not found with id: " + productId)))
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

  @Transactional
  public PromotionProduct addProduct(UUID promotionId, UUID productId)
      throws ResourceNotFoundException, InvalidRequestException {
    Promotion promotion = getPromotionById(promotionId);
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Unable to add product. Product not found with id: " + productId));
    PromotionProduct promotionProduct = new PromotionProduct();
    promotionProduct.setPromotion(promotion);
    promotionProduct.setProduct(product);
    authService.assertUserOwnsStore(product.getStore());
    authService.assertUserOwnsStore(promotion.getStore());

    try {
      return promotionProductRepository.save(promotionProduct);
    } catch (Exception e) {
      throw new InvalidRequestException(
          "This product is already part of the promotion or there was an error adding it.");
    }
  }

  @Transactional(readOnly = true)
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

  @Transactional
  public Promotion updatePromotion(UUID id, PromotionUpdateDTO dto)
      throws ResourceNotFoundException, InvalidRequestException {

    Promotion promotion = getPromotionById(id);
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

    if (dto.getProductIds() != null && !dto.getProductIds().isEmpty()) {
      Set<Product> products =
          dto.getProductIds().stream()
              .map(
                  productId ->
                      productRepository
                          .findById(productId)
                          .orElseThrow(
                              () ->
                                  new ResourceNotFoundException(
                                      "Product not found with id: " + productId)))
              .collect(java.util.stream.Collectors.toSet());

      if (products.stream()
          .anyMatch(product -> !product.getStore().getId().equals(promotion.getStore().getId()))) {
        throw new InvalidRequestException(
            "All products must belong to the same store as the promotion");
      }

      // Remove existing associations
      promotionProductRepository.findByPromotionId(id).forEach(promotionProductRepository::delete);

      // Add new associations
      products.forEach(product -> addProduct(id, product.getId()));
    }
    try {
      return promotionRepository.save(promotion);
    } catch (Exception e) {
      throw new InvalidRequestException("Error updating promotion");
    }
  }

  @Transactional
  public void deletePromotion(UUID id) {
    Promotion promotion = getPromotionById(id);
    authService.assertUserOwnsStore(promotion.getStore());
    promotionProductRepository.findByPromotionId(id).forEach(promotionProductRepository::delete);
    promotionRepository.delete(promotion);
  }

  @Transactional(readOnly = true)
  public List<UUID> getAllProductsByPromotionId(UUID promotionId) {
    getPromotionById(promotionId); // Ensure promotion exists
    return promotionProductRepository.findProductIdsByPromotionId(promotionId);
  }

  @Transactional(readOnly = true)
  public List<Promotion> getPromotionsByStoreId(UUID storeId) {
    return promotionRepository.findByStoreId(storeId);
  }

  @Transactional(readOnly = true)
  public List<Promotion> getPromotionsByProductId(UUID productId) {
    return promotionProductRepository.findPromotionsByProductId(productId);
  }
}
