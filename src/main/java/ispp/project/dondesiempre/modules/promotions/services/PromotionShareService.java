package ispp.project.dondesiempre.modules.promotions.services;

import ispp.project.dondesiempre.modules.common.exceptions.LimitExceededException;
import ispp.project.dondesiempre.modules.promotions.models.Promotion;
import ispp.project.dondesiempre.modules.promotions.models.PromotionShare;
import ispp.project.dondesiempre.modules.promotions.repositories.PromotionShareRepository;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PromotionShareService {

  private final PromotionService promotionService;
  private final PromotionShareRepository promotionShareRepository;

  @Transactional(rollbackFor = LimitExceededException.class)
  public void save(UUID id) throws LimitExceededException {
    Promotion promotion = promotionService.getPromotionById(id);
    boolean hasReachedLimit =
        promotionShareRepository.hasReachedMonthlyLimit(promotion, LocalDate.now().minusMonths(1));
    if (!promotion.getStore().getPremiumPlan() && hasReachedLimit) {
      throw new LimitExceededException();
    }
    PromotionShare promotionShare = new PromotionShare();
    promotionShare.setPromotion(promotion);
    promotionShare.setDate(LocalDate.now());
    promotionShareRepository.save(promotionShare);
  }
}
