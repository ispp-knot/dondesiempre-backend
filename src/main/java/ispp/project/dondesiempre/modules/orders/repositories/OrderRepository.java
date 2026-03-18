package ispp.project.dondesiempre.modules.orders.repositories;

import ispp.project.dondesiempre.modules.orders.models.Order;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, UUID> {
  List<Order> findByUserId(UUID userId);

  @Query("SELECT DISTINCT o FROM Order o JOIN o.items i JOIN i.product p WHERE p.store.id = :storeId")
  List<Order> findByStoreId(@Param("storeId") UUID storeId);
}
