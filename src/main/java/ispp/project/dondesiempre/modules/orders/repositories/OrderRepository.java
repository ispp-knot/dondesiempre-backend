package ispp.project.dondesiempre.modules.orders.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import ispp.project.dondesiempre.modules.orders.models.Order;

public interface OrderRepository extends JpaRepository<Order, UUID> {
  List<Order> findByUserId(UUID userId);
}