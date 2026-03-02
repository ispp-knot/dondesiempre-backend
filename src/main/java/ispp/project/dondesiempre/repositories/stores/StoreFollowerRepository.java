package ispp.project.dondesiempre.repositories.stores;

import ispp.project.dondesiempre.models.stores.StoreFollower;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreFollowerRepository extends JpaRepository<StoreFollower, UUID> {

  Optional<StoreFollower> findByClientIdAndStoreId(UUID clientId, UUID storeId);

  List<StoreFollower> findByClientId(UUID clientId);
}
