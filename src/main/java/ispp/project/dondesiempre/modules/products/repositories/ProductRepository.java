package ispp.project.dondesiempre.modules.products.repositories;

import ispp.project.dondesiempre.modules.products.models.Product;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, UUID> {

  @Query("SELECT p FROM Product p WHERE p.discountedPriceInCents != p.priceInCents")
  public List<Product> findAllDiscountedProducts();

  List<Product> findByStoreId(UUID storeId);

  @Query("select op.product from OutfitProduct op where op.outfit.id = :id order by op.index asc")
  public List<Product> findOutfitProductsByOutfitId(UUID id);

  @Query("SELECT pp.product FROM PromotionProduct pp WHERE pp.promotion.id = :promotionId")
  public List<Product> findProductsByPromotionId(UUID promotionId);
}
