package ispp.project.dondesiempre.repositories.stores;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import ispp.project.dondesiempre.models.stores.StoreSocialNetwork;

public interface StoreSocialNetworkRepository extends JpaRepository<StoreSocialNetwork, UUID> {

    List<StoreSocialNetwork> findByStoreId(UUID storeId);

}
