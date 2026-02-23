package ispp.project.dondesiempre.repositories.products;

import ispp.project.dondesiempre.models.products.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

  List<Category> findByStoreId(Integer storeId);

  boolean existsByNameAndStoreId(String name, Integer storeId);
}
