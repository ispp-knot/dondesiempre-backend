package ispp.project.dondesiempre.repositories.promotions;

import ispp.project.dondesiempre.models.promotions.Promotion;
import ispp.project.dondesiempre.models.promotions.PromotionProduct;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionProductRepository extends JpaRepository<PromotionProduct, UUID> {

  @Query("SELECT pp.product.id FROM PromotionProduct pp WHERE pp.promotion.id = :promotionId")
  public List<UUID> findProductIdsByPromotionId(UUID promotionId);

  @Query("SELECT pp FROM PromotionProduct pp WHERE pp.promotion.id = :promotionId")
  public List<PromotionProduct> findByPromotionId(UUID promotionId);

  @Query("SELECT pp.promotion FROM PromotionProduct pp WHERE pp.product.id = :productId")
  public List<Promotion> findPromotionsByProductId(UUID productId);
}
