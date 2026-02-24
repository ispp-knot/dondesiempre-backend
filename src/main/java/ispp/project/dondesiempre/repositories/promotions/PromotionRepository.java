package ispp.project.dondesiempre.repositories.promotions;

import ispp.project.dondesiempre.models.promotions.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {}
