package ispp.project.dondesiempre.repositories.collections;

import ispp.project.dondesiempre.models.collections.ProductCollection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionRepository extends JpaRepository<ProductCollection, UUID> {

  List<ProductCollection> findByStoreId(UUID storeId);

  boolean existsByNameAndStoreId(String name, UUID storeId);

  boolean existsByNameAndStoreIdAndIdNot(String name, UUID storeId, UUID id);
}
