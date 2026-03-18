package ispp.project.dondesiempre.modules.outfits.repositories;

import ispp.project.dondesiempre.modules.outfits.models.OutfitProduct;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OutfitProductRepository extends JpaRepository<OutfitProduct, UUID> {

  @Query("select op.index from OutfitProduct op where op.outfit.id = :id order by op.index asc")
  public List<Integer> findOutfitProductIndicesById(UUID id);

  List<OutfitProduct> findByOutfitIdOrderByIndexAsc(UUID outfitId);

  Optional<OutfitProduct> findByOutfitIdAndProductId(UUID outfitId, UUID productId);

  @Query(
      """
      SELECT op FROM OutfitProduct op
      JOIN FETCH op.product p
      JOIN FETCH p.type
      JOIN FETCH p.store
      WHERE op.outfit.id = :id
      ORDER BY op.index ASC
      """)
  List<OutfitProduct> findByOutfitIdWithDetails(@Param("id") UUID id);

  @Query(
      """
      SELECT op FROM OutfitProduct op
      JOIN FETCH op.product p
      JOIN FETCH p.type
      JOIN FETCH p.store
      WHERE op.outfit.id IN :ids
      ORDER BY op.index ASC
      """)
  List<OutfitProduct> findByOutfitIdsWithDetails(@Param("ids") Collection<UUID> ids);
}
