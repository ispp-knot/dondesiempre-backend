package ispp.project.dondesiempre.modules.outfits.repositories;

import ispp.project.dondesiempre.modules.outfits.models.OutfitProduct;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OutfitProductRepository extends JpaRepository<OutfitProduct, UUID> {

  @Query("select op.index from OutfitProduct op where op.outfit.id = :id order by op.index asc")
  public List<Integer> findOutfitProductIndicesById(UUID id);

  List<OutfitProduct> findByOutfitIdOrderByIndexAsc(UUID outfitId);

  Optional<OutfitProduct> findByOutfitIdAndProductId(UUID outfitId, UUID productId);
}
