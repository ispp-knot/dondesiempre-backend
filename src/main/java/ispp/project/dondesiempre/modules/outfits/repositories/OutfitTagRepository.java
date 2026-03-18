package ispp.project.dondesiempre.modules.outfits.repositories;

import ispp.project.dondesiempre.modules.outfits.models.OutfitTag;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OutfitTagRepository extends JpaRepository<OutfitTag, UUID> {

  Optional<OutfitTag> findByName(String name);

  @Query("select ot.tag.name from OutfitTagRelation ot where ot.outfit.id = :id")
  public List<String> findOutfitTagsByOutfitId(UUID id);

  interface OutfitTagEntry {
    UUID getOutfitId();

    String getTagName();
  }

  @Query(
      """
      SELECT ot.outfit.id AS outfitId, ot.tag.name AS tagName
      FROM OutfitTagRelation ot
      WHERE ot.outfit.id IN :ids
      """)
  List<OutfitTagEntry> findTagEntriesByOutfitIds(@Param("ids") Collection<UUID> ids);
}
