package ispp.project.dondesiempre.repositories.promotions;

import ispp.project.dondesiempre.models.promotions.Promotion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {

  @Query("SELECT p FROM Promotion p WHERE p.store.id = :storeId")
  public List<Promotion> findByStoreId(Integer storeId);
}
