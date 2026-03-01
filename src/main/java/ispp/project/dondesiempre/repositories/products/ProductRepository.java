package ispp.project.dondesiempre.repositories.products;

import ispp.project.dondesiempre.models.products.Product;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {

  List<Product> findByIdIn(Collection<Integer> ids);
}
