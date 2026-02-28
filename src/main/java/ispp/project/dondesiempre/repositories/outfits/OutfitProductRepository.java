package ispp.project.dondesiempre.repositories.outfits;

import ispp.project.dondesiempre.models.outfits.OutfitProduct;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutfitProductRepository extends JpaRepository<OutfitProduct, UUID> {}
