package ispp.project.dondesiempre.repositories.outfits;

import ispp.project.dondesiempre.models.outfits.OutfitTagRelation;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutfitTagRelationRepository extends JpaRepository<OutfitTagRelation, UUID> {}
