package ispp.project.dondesiempre.modules.follows.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import ispp.project.dondesiempre.modules.follows.models.StoreFollower;

public interface StoreFollowerRepository extends JpaRepository<StoreFollower, UUID> {

  Optional<StoreFollower> findByClientIdAndStoreId(UUID clientId, UUID storeId);

  List<StoreFollower> findByClientId(UUID clientId);

  boolean existsByClientIdAndStoreId(UUID clientId, UUID storeId);
}
