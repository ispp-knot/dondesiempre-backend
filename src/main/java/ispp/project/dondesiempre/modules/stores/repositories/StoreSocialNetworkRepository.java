package ispp.project.dondesiempre.modules.stores.repositories;

import ispp.project.dondesiempre.modules.stores.models.StoreSocialNetwork;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StoreSocialNetworkRepository extends JpaRepository<StoreSocialNetwork, UUID> {
  List<StoreSocialNetwork> findByStoreId(UUID storeId);

  @Query("SELECT s FROM StoreSocialNetwork s JOIN FETCH s.socialNetwork WHERE s.store.id IN :ids")
  List<StoreSocialNetwork> findByStoreIdsWithSocialNetwork(@Param("ids") Collection<UUID> ids);

  Boolean existsByStoreIdAndSocialNetworkId(UUID storeId, UUID socialNetworkId);

  Optional<StoreSocialNetwork> findByStoreIdAndSocialNetworkId(UUID storeId, UUID socialNetworkId);

  @Query("SELECT s FROM StoreSocialNetwork s JOIN FETCH s.socialNetwork WHERE s.id = :id")
  Optional<StoreSocialNetwork> findByIdWithSocialNetwork(@Param("id") UUID id);
}
