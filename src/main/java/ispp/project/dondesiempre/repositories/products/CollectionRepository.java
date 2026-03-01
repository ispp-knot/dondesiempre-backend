package ispp.project.dondesiempre.repositories.products;

import ispp.project.dondesiempre.models.products.ProductCollection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionRepository extends JpaRepository<ProductCollection, Integer> {

  List<ProductCollection> findByStoreId(Integer storeId);

  boolean existsByNameAndStoreId(String name, Integer storeId);

  boolean existsByNameAndStoreIdAndIdNot(String name, Integer storeId, Integer id);
}
