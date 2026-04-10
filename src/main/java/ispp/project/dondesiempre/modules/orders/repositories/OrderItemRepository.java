package ispp.project.dondesiempre.modules.orders.repositories;

import ispp.project.dondesiempre.modules.orders.models.OrderItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

  List<OrderItem> findByOrderId(UUID orderId);

  List<OrderItem> findByProductId(UUID productId);

  List<OrderItem> findByVariantId(UUID variantId);

  /**
   * Validates that a product variant belongs to a specific product. Returns true if the variant
   * belongs to the product, false otherwise.
   */
  @Query(
      "SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM ProductVariant v WHERE v.id = :variantId AND v.product.id = :productId")
  boolean isVariantBelongsToProduct(
      @Param("variantId") UUID variantId, @Param("productId") UUID productId);
}
