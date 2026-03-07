package ispp.project.dondesiempre.modules.stores.repositories;

import ispp.project.dondesiempre.modules.stores.models.SocialNetwork;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialNetworkRepository extends JpaRepository<SocialNetwork, UUID> {}
