package ispp.project.dondesiempre.modules.outfits.repositories;

import ispp.project.dondesiempre.modules.outfits.models.Outfit;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OutfitRepository extends JpaRepository<Outfit, UUID> {

  List<Outfit> findByStoreIdOrderByIndexAsc(UUID storeId);

  @Query(
      "SELECT o FROM Outfit o WHERE o.store.id = :storeId AND LOWER(o.name) LIKE LOWER(CONCAT('%', :name, '%')) ESCAPE '\\' ORDER BY o.index ASC")
  List<Outfit> findByStoreIdAndNameContainingIgnoreCase(
      @Param("storeId") UUID storeId, @Param("name") String name);
}
