package ispp.project.dondesiempre.repositories.outfits;

import ispp.project.dondesiempre.models.outfits.OutfitTag;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OutfitTagRepository extends JpaRepository<OutfitTag, UUID> {
  @Query("select ot from OutfitTag ot where ot.name like :name")
  public Optional<OutfitTag> findByName(String name);
}
