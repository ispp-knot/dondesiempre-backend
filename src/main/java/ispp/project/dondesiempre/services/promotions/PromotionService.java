package ispp.project.dondesiempre.services.promotions;

import ispp.project.dondesiempre.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.models.promotions.Promotion;
import ispp.project.dondesiempre.models.promotions.PromotionProduct;
import ispp.project.dondesiempre.models.promotions.dto.PromotionCreationDTO;
import ispp.project.dondesiempre.repositories.products.ProductRepository;
import ispp.project.dondesiempre.repositories.promotions.PromotionProductRepository;
import ispp.project.dondesiempre.repositories.promotions.PromotionRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PromotionService {

  private final PromotionRepository promotionRepository;
  private final ProductRepository productRepository;
  private final PromotionProductRepository promotionProductRepository;

  @Transactional
  public Promotion savePromotion(PromotionCreationDTO dto) {
    Promotion promotion = new Promotion();
    promotion.setName(dto.getName());
    if (dto.getDiscountPercentage() < 1 || dto.getDiscountPercentage() > 100) {
      throw new InvalidRequestException("Discount must be between 1 and 100");
    }
    promotion.setActive(dto.isActive());
    promotion.setDiscountPercentage(dto.getDiscountPercentage());
    promotion.setDescription(dto.getDescription());

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

    Promotion saved = promotionRepository.save(promotion);

    products.forEach(product -> addProduct(saved.getId(), product.getId()));

    return saved;
  }

  @Transactional
  public PromotionProduct addProduct(Integer promotionId, Integer productId) {
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
    return promotionProductRepository.save(promotionProduct);
  }

  @Transactional(readOnly = true)
  public Promotion getPromotionById(Integer id) {
    return promotionRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with id: " + id));
  }

  @Transactional(readOnly = true)
  public List<Promotion> getAllPromotions() {
    return promotionRepository.findAll();
  }

  public Integer calculateDiscountedPrice(Integer originalPrice, Integer discountPercentage) {
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
  public Promotion updatePromotionDiscount(Integer id, Integer discountPercentage) {
    if (discountPercentage < 1 || discountPercentage > 100) {
      throw new InvalidRequestException("Discount must be between 1 and 100");
    }
    Promotion promotion = getPromotionById(id);
    promotion.setDiscountPercentage(discountPercentage);
    return promotionRepository.save(promotion);
  }

  @Transactional
  public void deletePromotion(Integer id) {
    Promotion promotion = getPromotionById(id);
    promotionRepository.delete(promotion);
  }

  @Transactional(readOnly = true)
  public List<Integer> getAllProductsByPromotionId(Integer promotionId) {
    getPromotionById(promotionId); // Ensure promotion exists
    return promotionProductRepository.findProductIdsByPromotionId(promotionId);
  }

  @Transactional(readOnly = true)
  public List<Promotion> getPromotionsByStoreId(Integer storeId) {
    return promotionRepository.findByStoreId(storeId);
  }
}
