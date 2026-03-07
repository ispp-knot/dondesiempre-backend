package ispp.project.dondesiempre.modules.stores.repositories;

import ispp.project.dondesiempre.modules.stores.models.Storefront;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorefrontRepository extends JpaRepository<Storefront, UUID> {}
