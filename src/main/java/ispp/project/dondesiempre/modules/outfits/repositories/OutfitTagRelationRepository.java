package ispp.project.dondesiempre.modules.outfits.repositories;

import ispp.project.dondesiempre.modules.outfits.models.OutfitTagRelation;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutfitTagRelationRepository extends JpaRepository<OutfitTagRelation, UUID> {}
