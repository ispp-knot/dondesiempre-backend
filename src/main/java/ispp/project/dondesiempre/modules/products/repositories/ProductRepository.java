package ispp.project.dondesiempre.modules.products.repositories;

import ispp.project.dondesiempre.modules.products.models.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, UUID> {

  public List<Product> findByDiscountPercentageIsNotNullAndIsDeletedIsFalse();

  List<Product> findByStoreIdAndIsDeletedIsFalse(UUID storeId);

  Optional<Product> findByIdAndIsDeletedIsFalse(UUID id);

  List<Product> findByIsDeletedIsFalse();

  @Query(
      "select op.product from OutfitProduct op where op.outfit.id = :id AND op.product.isDeleted = false order by op.index asc")
  public List<Product> findOutfitProductsByOutfitId(UUID id);

  @Query(
      "SELECT pp.product FROM PromotionProduct pp WHERE pp.promotion.id = :promotionId AND pp.product.isDeleted = false")
  public List<Product> findProductsByPromotionId(UUID promotionId);

  @Query(
      "SELECT p FROM Product p WHERE p.store.id = :storeId AND p.isDeleted = false AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
  List<Product> findByStoreIdAndNameContainingIgnoreCase(
      @Param("storeId") UUID storeId, @Param("name") String name);
}
