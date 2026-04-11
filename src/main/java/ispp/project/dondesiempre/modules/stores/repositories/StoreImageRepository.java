package ispp.project.dondesiempre.modules.stores.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import ispp.project.dondesiempre.modules.stores.models.StoreImage;
import ispp.project.dondesiempre.modules.stores.models.Storefront;

import java.util.List;
import java.util.UUID;

public interface StoreImageRepository  extends JpaRepository<StoreImage, UUID> {
    List<StoreImage> findImagesByStoreIdOrderByDisplayOrder(UUID storeId);
}
