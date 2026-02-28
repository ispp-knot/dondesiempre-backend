package ispp.project.dondesiempre.repositories.promotions;

import ispp.project.dondesiempre.models.promotions.Promotion;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, UUID> {

  @Query("SELECT p FROM Promotion p WHERE p.store.id = :storeId")
  public List<Promotion> findByStoreId(UUID storeId);
}
