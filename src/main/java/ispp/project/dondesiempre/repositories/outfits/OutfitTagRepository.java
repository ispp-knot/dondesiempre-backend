package ispp.project.dondesiempre.repositories.outfits;

import ispp.project.dondesiempre.models.outfits.OutfitTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OutfitTagRepository extends JpaRepository<OutfitTag, Integer> {
  @Query("select ot from OutfitTag ot where ot.name like :name")
  public OutfitTag findByName(String name);
}
