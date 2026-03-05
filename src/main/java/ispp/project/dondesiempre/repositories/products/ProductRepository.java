package ispp.project.dondesiempre.repositories.products;

import ispp.project.dondesiempre.models.products.Product;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, UUID> {

  List<Product> findByIdIn(Collection<UUID> ids);

  @Query("SELECT p FROM Product p WHERE p.discountedPriceInCents != p.priceInCents")
  List<Product> findAllDiscountedProducts();

  @Query("select p from Product p where p.store.storefront.id = :storefrontId")
  List<Product> findByStorefrontId(UUID storefrontId);
}
