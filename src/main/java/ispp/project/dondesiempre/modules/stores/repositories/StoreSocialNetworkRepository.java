package ispp.project.dondesiempre.modules.stores.repositories;

import ispp.project.dondesiempre.modules.stores.models.StoreSocialNetwork;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreSocialNetworkRepository extends JpaRepository<StoreSocialNetwork, UUID> {
  List<StoreSocialNetwork> findByStoreId(UUID storeId);
}
