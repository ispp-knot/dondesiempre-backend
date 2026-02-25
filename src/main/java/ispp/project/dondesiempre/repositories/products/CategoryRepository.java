package ispp.project.dondesiempre.repositories.products;

import ispp.project.dondesiempre.models.products.ProductCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<ProductCategory, Integer> {

  List<ProductCategory> findByStoreId(Integer storeId);

  boolean existsByNameAndStoreId(String name, Integer storeId);
}
