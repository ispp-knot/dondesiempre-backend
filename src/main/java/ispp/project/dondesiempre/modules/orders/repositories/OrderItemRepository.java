package ispp.project.dondesiempre.modules.orders.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import ispp.project.dondesiempre.modules.orders.models.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
}