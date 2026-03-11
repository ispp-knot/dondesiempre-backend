package ispp.project.dondesiempre.modules.outfits.repositories;

import ispp.project.dondesiempre.modules.outfits.models.Outfit;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutfitRepository extends JpaRepository<Outfit, UUID> {

  List<Outfit> findByStoreIdOrderByIndexAsc(UUID storeId);
}
