package ispp.project.dondesiempre.modules.follows.repositories;

import ispp.project.dondesiempre.modules.follows.models.StoreFollower;
import ispp.project.dondesiempre.modules.stores.models.Store;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StoreFollowerRepository extends JpaRepository<StoreFollower, UUID> {

  Optional<StoreFollower> findByClientIdAndStoreId(UUID clientId, UUID storeId);

  List<StoreFollower> findByClientId(UUID clientId);

  @Query("SELECT f.store FROM StoreFollower f WHERE f.client.id = :clientId")
  List<Store> findStoresByClientId(@Param("clientId") UUID clientId);

  boolean existsByClientIdAndStoreId(UUID clientId, UUID storeId);
}
