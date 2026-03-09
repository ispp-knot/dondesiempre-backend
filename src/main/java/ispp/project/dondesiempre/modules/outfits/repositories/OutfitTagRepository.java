package ispp.project.dondesiempre.modules.outfits.repositories;

import ispp.project.dondesiempre.modules.outfits.models.OutfitTag;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OutfitTagRepository extends JpaRepository<OutfitTag, UUID> {
  @Query("select ot from OutfitTag ot where ot.name like :name")
  public Optional<OutfitTag> findByName(String name);

  @Query("select ot.tag.name from OutfitTagRelation ot where ot.outfit.id = :id")
  public List<String> findOutfitTagsById(UUID id);
}
