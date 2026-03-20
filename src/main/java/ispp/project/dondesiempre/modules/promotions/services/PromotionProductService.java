package ispp.project.dondesiempre.modules.promotions.services;

import ispp.project.dondesiempre.modules.promotions.models.PromotionProduct;
import ispp.project.dondesiempre.modules.promotions.repositories.PromotionProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PromotionProductService {

  private final PromotionProductRepository promotionProductRepository;
  @PersistenceContext private EntityManager entityManager;

  @Transactional
  public PromotionProduct save(PromotionProduct promotionProduct) {
    return promotionProductRepository.save(promotionProduct);
  }

  @Transactional(readOnly = true)
  public List<PromotionProduct> findByPromotionId(UUID promotionId) {
    return promotionProductRepository.findByPromotionId(promotionId);
  }

  @Transactional
  public void delete(PromotionProduct promotionProduct) {
    promotionProductRepository.delete(promotionProduct);
  }

  @Transactional
  public void flushChanges() {
    entityManager.flush();
  }
}
