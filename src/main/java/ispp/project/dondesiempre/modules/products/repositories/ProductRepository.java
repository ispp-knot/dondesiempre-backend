package ispp.project.dondesiempre.modules.products.repositories;

import ispp.project.dondesiempre.modules.products.models.Product;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, UUID> {

  @Query("SELECT p FROM Product p WHERE p.discountedPriceInCents != p.priceInCents")
  public List<Product> findAllDiscountedProducts();

  @Query("select p from Product p where p.store.storefront.id = :storefrontId")
  public List<Product> findByStorefrontId(UUID storefrontId);
}
