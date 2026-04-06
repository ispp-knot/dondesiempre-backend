package ispp.project.dondesiempre.modules.promotions.repositories;

import ispp.project.dondesiempre.modules.promotions.models.Promotion;
import ispp.project.dondesiempre.modules.promotions.models.PromotionShare;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionShareRepository extends JpaRepository<PromotionShare, UUID> {
  @Query(
      "SELECT COUNT(ps) >= 2 FROM PromotionShare ps WHERE ps.promotion = :promotion AND ps.date >= :oneMonthAgo")
  boolean hasReachedMonthlyLimit(
      @Param("promotion") Promotion promotion, @Param("oneMonthAgo") LocalDate oneMonthAgo);
}
