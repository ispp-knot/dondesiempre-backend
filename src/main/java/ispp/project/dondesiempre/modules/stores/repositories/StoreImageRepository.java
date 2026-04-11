package ispp.project.dondesiempre.modules.stores.repositories;

import ispp.project.dondesiempre.modules.stores.models.StoreImage;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreImageRepository extends JpaRepository<StoreImage, UUID> {
  List<StoreImage> findImagesByStoreIdOrderByDisplayOrder(UUID storeId);
}
