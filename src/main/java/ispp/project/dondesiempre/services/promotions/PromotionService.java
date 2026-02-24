package ispp.project.dondesiempre.services.promotions;

import ispp.project.dondesiempre.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.models.promotions.Promotion;
import ispp.project.dondesiempre.models.promotions.dto.PromotionCreationDTO;
import ispp.project.dondesiempre.repositories.products.ProductRepository;
import ispp.project.dondesiempre.repositories.promotions.PromotionRepository;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PromotionService {

  private final PromotionRepository promotionRepository;
  private final ProductRepository productRepository;

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

    Set<Product> products = new HashSet<>();
    for (Integer productId : dto.getProductIds()) {
      Product product =
          productRepository
              .findById(productId)
              .orElseThrow(
                  () -> new ResourceNotFoundException("Product not found with id: " + productId));
      products.add(product);
    }
    promotion.setProducts(products);

    return promotionRepository.save(promotion);
  }

  public Promotion getPromotionById(Integer id) {
    return promotionRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with id: " + id));
  }

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
}
