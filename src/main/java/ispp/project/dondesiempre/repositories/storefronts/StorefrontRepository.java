package ispp.project.dondesiempre.repositories.storefronts;

import java.util.UUID;

import ispp.project.dondesiempre.models.storefronts.Storefront;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorefrontRepository extends JpaRepository<Storefront, UUID> {}
