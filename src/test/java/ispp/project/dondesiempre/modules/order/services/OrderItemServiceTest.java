package ispp.project.dondesiempre.modules.order.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.common.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.modules.orders.dtos.OrderDTO.OrderItemDTO;
import ispp.project.dondesiempre.modules.orders.models.OrderItem;
import ispp.project.dondesiempre.modules.orders.repositories.OrderItemRepository;
import ispp.project.dondesiempre.modules.orders.services.OrderItemService;
import ispp.project.dondesiempre.modules.products.models.Product;
import ispp.project.dondesiempre.modules.products.models.ProductColor;
import ispp.project.dondesiempre.modules.products.models.ProductSize;
import ispp.project.dondesiempre.modules.products.models.ProductVariant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderItemServiceTest {

  @Mock private OrderItemRepository orderItemRepository;

  @InjectMocks private OrderItemService orderItemService;

  private UUID itemId;
  private UUID variantId;
  private OrderItem item;
  private Product product;
  private ProductVariant variant;

  @BeforeEach
  void setUp() {
    itemId = UUID.randomUUID();
    variantId = UUID.randomUUID();

    product = new Product();
    product.setId(UUID.randomUUID());
    product.setName("Producto Test");

    // Create variant
    ProductSize size = new ProductSize();
    size.setSize("M");

    ProductColor color = new ProductColor();
    color.setColor("Red");

    variant = new ProductVariant();
    variant.setId(variantId);
    variant.setProduct(product);
    variant.setSize(size);
    variant.setColor(color);
    variant.setIsAvailable(true);

    item = new OrderItem();
    item.setId(itemId);
    item.setProduct(product);
    item.setVariant(variant);
    item.setQuantity(3);
    item.setPriceAtPurchase(100);
  }

  @Test
  void shouldReturnEntity_whenIdExists() throws ResourceNotFoundException {
    when(orderItemRepository.findById(itemId)).thenReturn(Optional.of(item));

    OrderItem result = orderItemService.findEntityById(itemId);

    assertNotNull(result);
    assertEquals(itemId, result.getId());
  }

  @Test
  void shouldThrowException_whenEntityNotFound() {
    when(orderItemRepository.findById(itemId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> orderItemService.findEntityById(itemId));
  }

  @Test
  void shouldFindItemsByOrderId_andMapToDTO() {
    UUID orderId = UUID.randomUUID();
    when(orderItemRepository.findByOrderId(orderId)).thenReturn(List.of(item));

    List<OrderItemDTO> dtos = orderItemService.findItemsByOrderId(orderId);

    assertNotNull(dtos);
    assertEquals(1, dtos.size());
    assertEquals(product.getName(), dtos.get(0).getProductName());
    assertEquals(300, dtos.get(0).getSubtotal());
  }

  @Test
  void shouldFindItemsByProductId_andMapToDTO() {
    UUID productId = product.getId();
    when(orderItemRepository.findByProductId(productId)).thenReturn(List.of(item));

    List<OrderItemDTO> dtos = orderItemService.findItemsByProductId(productId);

    assertNotNull(dtos);
    assertEquals(1, dtos.size());
    assertEquals(productId, dtos.get(0).getProductId());
  }

  @Test
  void shouldSaveOrderItem() {
    when(orderItemRepository.isVariantBelongsToProduct(variantId, product.getId()))
        .thenReturn(true);
    when(orderItemRepository.save(item)).thenReturn(item);

    OrderItem saved = orderItemService.saveOrderItem(item);

    assertNotNull(saved);
    verify(orderItemRepository, times(1)).save(item);
  }

  @Test
  void shouldDeleteOrderItem() {
    orderItemService.deleteOrderItem(itemId);
    verify(orderItemRepository, times(1)).deleteById(itemId);
  }

  // Variant-related tests
  @Test
  void shouldSaveOrderItem_WhenVariantBelongsToProductAndIsAvailable()
      throws UnauthorizedException {
    when(orderItemRepository.isVariantBelongsToProduct(variantId, product.getId()))
        .thenReturn(true);
    when(orderItemRepository.save(any(OrderItem.class))).thenReturn(item);

    OrderItem result = orderItemService.saveOrderItem(item);

    assertEquals(itemId, result.getId());
    verify(orderItemRepository, times(1)).isVariantBelongsToProduct(variantId, product.getId());
    verify(orderItemRepository, times(1)).save(item);
  }

  @Test
  void shouldThrowUnauthorizedException_WhenVariantDoesNotBelongToProduct()
      throws UnauthorizedException {
    when(orderItemRepository.isVariantBelongsToProduct(variantId, product.getId()))
        .thenReturn(false);

    assertThrows(UnauthorizedException.class, () -> orderItemService.saveOrderItem(item));

    verify(orderItemRepository, times(1)).isVariantBelongsToProduct(variantId, product.getId());
    verify(orderItemRepository, never()).save(any(OrderItem.class));
  }

  @Test
  void shouldThrowUnauthorizedException_WhenVariantIsNotAvailable() throws UnauthorizedException {
    variant.setIsAvailable(false);

    when(orderItemRepository.isVariantBelongsToProduct(variantId, product.getId()))
        .thenReturn(true);

    assertThrows(UnauthorizedException.class, () -> orderItemService.saveOrderItem(item));

    verify(orderItemRepository, times(1)).isVariantBelongsToProduct(variantId, product.getId());
    verify(orderItemRepository, never()).save(any(OrderItem.class));
  }

  @Test
  void shouldFindItemsByVariantId() {
    when(orderItemRepository.findByVariantId(variantId)).thenReturn(List.of(item));

    List<OrderItemDTO> result = orderItemService.findItemsByVariantId(variantId);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(variantId, result.get(0).getVariantId());
    assertEquals("M", result.get(0).getVariantSize());
    assertEquals("Red", result.get(0).getVariantColor());
  }

  @Test
  void shouldMapOrderItemToDTO_WithVariantDetails() {
    UUID orderId = UUID.randomUUID();
    when(orderItemRepository.findByOrderId(orderId)).thenReturn(List.of(item));

    List<OrderItemDTO> dtos = orderItemService.findItemsByOrderId(orderId);

    assertEquals(1, dtos.size());
    OrderItemDTO dto = dtos.get(0);
    assertEquals(product.getName(), dto.getProductName());
    assertEquals(variantId, dto.getVariantId());
    assertEquals("M", dto.getVariantSize());
    assertEquals("Red", dto.getVariantColor());
    assertEquals(3, dto.getQuantity());
    assertEquals(300, dto.getSubtotal());
  }
}
