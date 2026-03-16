package ispp.project.dondesiempre.modules.orders.repositories;

import ispp.project.dondesiempre.modules.orders.models.OrderItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

  List<OrderItem> findByOrderId(UUID orderId);

  List<OrderItem> findByProductId(UUID productId);
}
