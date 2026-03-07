package ispp.project.dondesiempre.modules.stores.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import ispp.project.dondesiempre.modules.stores.models.SocialNetwork;

public interface SocialNetworkRepository extends JpaRepository<SocialNetwork, UUID> {
}
