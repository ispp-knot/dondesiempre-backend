package ispp.project.dondesiempre.modules.orders.repositories;

import ispp.project.dondesiempre.modules.orders.models.Order;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID> {
  List<Order> findByUserId(UUID userId);
}
