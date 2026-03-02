package ispp.project.dondesiempre.repositories.stores;

import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.models.stores.StoreSocialNetwork;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreSocialNetworkRepository extends JpaRepository<StoreSocialNetwork, UUID> {
  List<StoreSocialNetwork> findByStore(Store store);
}
