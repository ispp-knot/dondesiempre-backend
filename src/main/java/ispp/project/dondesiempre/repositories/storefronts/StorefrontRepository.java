package ispp.project.dondesiempre.repositories.storefronts;

import ispp.project.dondesiempre.models.storefronts.Storefront;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorefrontRepository extends JpaRepository<Storefront, UUID> {}
