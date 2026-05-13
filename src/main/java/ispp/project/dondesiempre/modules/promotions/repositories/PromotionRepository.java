package ispp.project.dondesiempre.modules.promotions.repositories;

import ispp.project.dondesiempre.modules.promotions.models.Promotion;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, UUID> {

  List<Promotion> findByStoreId(UUID storeId);

  @Query("SELECT pp.promotion FROM PromotionProduct pp WHERE pp.product.id = :productId")
  public List<Promotion> findPromotionsByProductId(UUID productId);

  @Query(
      "SELECT COUNT(p) > 0 FROM Promotion p WHERE p.store.id = :storeId "
          + "AND (p.startDate IS NULL OR p.startDate <= CURRENT_DATE) "
          + "AND (p.endDate IS NULL OR p.endDate >= CURRENT_DATE)")
  boolean hasActivePromotions(UUID storeId);
}
