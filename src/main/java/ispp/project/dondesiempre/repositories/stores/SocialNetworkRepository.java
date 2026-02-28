package ispp.project.dondesiempre.repositories.stores;

import ispp.project.dondesiempre.models.stores.SocialNetwork;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialNetworkRepository extends JpaRepository<SocialNetwork, UUID> {}
