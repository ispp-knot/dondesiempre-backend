package ispp.project.dondesiempre.modules.promotions.repositories;

import ispp.project.dondesiempre.modules.promotions.models.PromotionProduct;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionProductRepository extends JpaRepository<PromotionProduct, UUID> {

  @Query("SELECT pp FROM PromotionProduct pp WHERE pp.promotion.id = :promotionId")
  public List<PromotionProduct> findByPromotionId(UUID promotionId);
}
