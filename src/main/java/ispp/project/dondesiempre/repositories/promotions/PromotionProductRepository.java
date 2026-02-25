package ispp.project.dondesiempre.repositories.promotions;

import ispp.project.dondesiempre.models.promotions.PromotionProduct;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionProductRepository extends JpaRepository<PromotionProduct, Integer> {

  @Query("SELECT pp.product.id FROM PromotionProduct pp WHERE pp.promotion.id = :promotionId")
  public List<Integer> findProductIdsByPromotionId(Integer promotionId);
}
