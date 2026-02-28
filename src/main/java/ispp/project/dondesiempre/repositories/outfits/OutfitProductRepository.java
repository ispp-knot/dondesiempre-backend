package ispp.project.dondesiempre.repositories.outfits;

import java.util.UUID;

import ispp.project.dondesiempre.models.outfits.OutfitProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutfitProductRepository extends JpaRepository<OutfitProduct, UUID> {}
