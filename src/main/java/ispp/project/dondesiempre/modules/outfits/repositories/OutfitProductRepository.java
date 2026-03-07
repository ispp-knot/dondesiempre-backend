package ispp.project.dondesiempre.modules.outfits.repositories;

import ispp.project.dondesiempre.modules.outfits.models.OutfitProduct;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutfitProductRepository extends JpaRepository<OutfitProduct, UUID> {}
