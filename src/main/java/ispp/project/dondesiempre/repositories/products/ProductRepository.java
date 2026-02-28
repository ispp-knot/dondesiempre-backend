package ispp.project.dondesiempre.repositories.products;

import ispp.project.dondesiempre.models.products.Product;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, UUID> {

  @Query("SELECT p FROM Product p WHERE p.discountedPriceInCents != p.priceInCents")
  public List<Product> findAllDiscountedProducts();
}
