package ispp.project.dondesiempre.modules.orders.services;

import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.common.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.modules.orders.dtos.OrderDTO.OrderItemDTO;
import ispp.project.dondesiempre.modules.orders.models.OrderItem;
import ispp.project.dondesiempre.modules.orders.repositories.OrderItemRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderItemService {

  private final OrderItemRepository orderItemRepository;

  @Transactional(readOnly = true)
  public List<OrderItem> findAllEntities() {
    return orderItemRepository.findAll();
  }

  @Transactional(readOnly = true, rollbackFor = ResourceNotFoundException.class)
  public OrderItem findEntityById(UUID id) throws ResourceNotFoundException {
    return orderItemRepository
        .findById(id)
        .orElseThrow(
            () -> new ResourceNotFoundException("OrderItem con ID " + id + " no encontrado."));
  }

  @Transactional
  public OrderItem saveOrderItem(OrderItem orderItem) throws UnauthorizedException {
    validateVariantBelongsToProduct(orderItem.getVariant().getId(), orderItem.getProduct().getId());
    validateVariantIsAvailable(orderItem.getVariant());
    return orderItemRepository.save(orderItem);
  }

  @Transactional
  public void deleteOrderItem(UUID id) {
    orderItemRepository.deleteById(id);
  }

  @Transactional(readOnly = true)
  public List<OrderItemDTO> findAllOrderItems() {
    return orderItemRepository.findAll().stream().map(this::mapToDTO).toList();
  }

  @Transactional(readOnly = true)
  public List<OrderItemDTO> findItemsByOrderId(UUID orderId) {
    return orderItemRepository.findByOrderId(orderId).stream().map(this::mapToDTO).toList();
  }

  @Transactional(readOnly = true)
  public List<OrderItemDTO> findItemsByProductId(UUID productId) {
    return orderItemRepository.findByProductId(productId).stream().map(this::mapToDTO).toList();
  }

  @Transactional(readOnly = true)
  public List<OrderItemDTO> findItemsByVariantId(UUID variantId) {
    return orderItemRepository.findByVariantId(variantId).stream().map(this::mapToDTO).toList();
  }

  /**
   * Validates that the provided variant belongs to the product. Throws UnauthorizedException if
   * not.
   */
  private void validateVariantBelongsToProduct(UUID variantId, UUID productId)
      throws UnauthorizedException {
    if (!orderItemRepository.isVariantBelongsToProduct(variantId, productId)) {
      throw new UnauthorizedException(
          String.format(
              "ProductVariant with ID %s does not belong to Product with ID %s",
              variantId, productId));
    }
  }

  /** Validates that the variant is available for purchase. Throws UnauthorizedException if not. */
  private void validateVariantIsAvailable(
      ispp.project.dondesiempre.modules.products.models.ProductVariant variant)
      throws UnauthorizedException {
    if (!variant.getIsAvailable()) {
      throw new UnauthorizedException(
          String.format(
              "ProductVariant with ID %s is not available for purchase", variant.getId()));
    }
  }

  private OrderItemDTO mapToDTO(OrderItem item) {
    return OrderItemDTO.builder()
        .id(item.getId())
        .productId(item.getProduct().getId())
        .productName(item.getProduct().getName())
        .variantId(item.getVariant().getId())
        .variantSize(item.getVariant().getSize().getSize())
        .variantColor(item.getVariant().getColor().getColor())
        .quantity(item.getQuantity())
        .priceAtPurchase(item.getPriceAtPurchase())
        .subtotal(item.getQuantity() * item.getPriceAtPurchase())
        .build();
  }
}
