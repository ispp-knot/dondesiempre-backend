package ispp.project.dondesiempre.repositories.stores;

import java.util.UUID;

import ispp.project.dondesiempre.models.stores.StoreSocialNetwork;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreSocialNetworkRepository extends JpaRepository<StoreSocialNetwork, UUID> {}
