package ispp.project.dondesiempre.repositories.products;

import ispp.project.dondesiempre.models.products.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Integer> {

  @Query("SELECT p FROM Product p WHERE p.discount > 0")
  public List<Product> findAllDiscountedProducts();
}
