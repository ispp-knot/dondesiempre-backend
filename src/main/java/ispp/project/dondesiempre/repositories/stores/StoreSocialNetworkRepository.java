package ispp.project.dondesiempre.repositories.stores;

import ispp.project.dondesiempre.models.stores.StoreSocialNetwork;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreSocialNetworkRepository extends JpaRepository<StoreSocialNetwork, UUID> {}
