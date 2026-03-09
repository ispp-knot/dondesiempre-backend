package ispp.project.dondesiempre.modules.outfits.repositories;

import ispp.project.dondesiempre.modules.outfits.models.OutfitTagRelation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OutfitTagRelationRepository extends JpaRepository<OutfitTagRelation, UUID> {

    @Query("select ot from OutfitTagRelation ot where ot.outfit.id = :id")
    public List<OutfitTagRelation> findOutfitTagsById(UUID id);

    @Query("select ot from OutfitTagRelation ot where ot.outfit.id = :id and ot.tag.id = :tagId")
    public Optional<OutfitTagRelation> findTagRelation(UUID id, UUID tagId);
}
