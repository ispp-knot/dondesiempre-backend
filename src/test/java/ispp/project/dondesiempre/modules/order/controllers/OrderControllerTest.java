package ispp.project.dondesiempre.modules.order.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import ispp.project.dondesiempre.config.GlobalExceptionHandler;
import ispp.project.dondesiempre.modules.common.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.modules.orders.controllers.OrderController;
import ispp.project.dondesiempre.modules.orders.dtos.OrderDTO;
import ispp.project.dondesiempre.modules.orders.dtos.OrderDTO.OrderItemDTO;
import ispp.project.dondesiempre.modules.orders.models.OrderStatus;
import ispp.project.dondesiempre.modules.orders.services.OrderService;
import ispp.project.dondesiempre.modules.products.models.Product;
import ispp.project.dondesiempre.modules.products.services.ProductService;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    controllers = OrderController.class,
    excludeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {GlobalExceptionHandler.class}))
class OrderControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private OrderService orderService;
  @MockitoBean private ProductService productService;

  private UUID orderId;
  private UUID productId;
  private UUID variantId;
  private OrderDTO orderDTO;
  private Product product;
  private String orderCode;

  @BeforeEach
  void setUp() {
    orderId = UUID.randomUUID();
    productId = UUID.randomUUID();
    variantId = UUID.randomUUID();
    orderCode = "ABCD-1234-EFGH";

    // Create order item DTO with variant information
    OrderItemDTO itemDTO =
        OrderItemDTO.builder()
            .id(UUID.randomUUID())
            .productId(productId)
            .productName("Producto Test")
            .variantId(variantId)
            .variantSize("M")
            .variantColor("Red")
            .quantity(2)
            .priceAtPurchase(750)
            .subtotal(1500)
            .build();

    orderDTO =
        OrderDTO.builder()
            .id(orderId)
            .orderCode(orderCode)
            .orderDate(LocalDateTime.now())
            .orderStatus(OrderStatus.PENDING)
            .totalPrice(1500)
            .items(List.of(itemDTO))
            .build();

    product = new Product();
    product.setId(productId);
    product.setName("Producto Test");
    product.setPriceInCents(750);
  }

  @Test
  @WithMockUser
  void getMyOrders_shouldReturnOk() throws Exception {
    when(orderService.findOrdersOfCurrenUser()).thenReturn(List.of(orderDTO));

    mockMvc
        .perform(get("/api/v1/orders").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1))
        .andExpect(jsonPath("$[0].id").value(orderId.toString()))
        .andExpect(jsonPath("$[0].orderCode").value(orderCode));

    verify(orderService, times(1)).findOrdersOfCurrenUser();
  }

  @Test
  @WithMockUser
  void findOrder_shouldReturnOk() throws Exception {
    when(orderService.findOrder(orderCode)).thenReturn(orderDTO);

    mockMvc
        .perform(
            get("/api/v1/orders/pick/{orderCode}", orderCode)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(orderId.toString()))
        .andExpect(jsonPath("$.orderCode").value(orderCode));

    verify(orderService, times(1)).findOrder(orderCode);
  }

  @Test
  @WithMockUser
  void createOrder_shouldReturnCreated() throws Exception {
    Map<UUID, Integer> payload = Map.of(variantId, 1);
    when(orderService.createOrder(any(Map.class), any(), any())).thenReturn(orderDTO);
    mockMvc
        .perform(
            post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(orderId.toString()));
    verify(orderService, times(1)).createOrder(any(Map.class), any(), any());
  }

  @Test
  @WithMockUser
  void createOrder_withOutfit_shouldReturnCreated() throws Exception {
    UUID outfitId = UUID.randomUUID();
    Map<UUID, Integer> payload = Map.of(variantId, 1);
    when(orderService.createOrder(any(Map.class), eq(outfitId), any())).thenReturn(orderDTO);
    mockMvc
        .perform(
            post("/api/v1/orders")
                .param("outfitId", outfitId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(orderId.toString()));
    verify(orderService, times(1)).createOrder(any(Map.class), eq(outfitId), any());
  }

  @Test
  @WithMockUser
  void confirmOrder_shouldReturnNoContent() throws Exception {
    doNothing().when(orderService).confirmOrder(orderId);

    mockMvc
        .perform(
            patch("/api/v1/orders/{orderId}/confirm", orderId)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    verify(orderService, times(1)).confirmOrder(orderId);
  }

  @Test
  @WithMockUser
  void rejectOrder_shouldReturnNoContent() throws Exception {
    doNothing().when(orderService).rejectOrder(orderId);

    mockMvc
        .perform(
            patch("/api/v1/orders/{orderId}/reject", orderId)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    verify(orderService, times(1)).rejectOrder(orderId);
  }

  @Test
  @WithMockUser
  void pickOrder_shouldReturnNoContent() throws Exception {
    doNothing().when(orderService).pickOrder(orderId);

    mockMvc
        .perform(
            patch("/api/v1/orders/{orderId}/pick", orderId).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    verify(orderService, times(1)).pickOrder(orderId);
  }

  // Variant-related tests
  @Test
  @WithMockUser
  void createOrder_WithVariants_ShouldReturnOrderWithVariantDetails() throws Exception {
    Map<UUID, Integer> variantIdsWithQuantity = new HashMap<>();
    variantIdsWithQuantity.put(variantId, 2);

    when(orderService.createOrder(any(Map.class), any(), any())).thenReturn(orderDTO);

    mockMvc
        .perform(
            post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(variantIdsWithQuantity)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(orderId.toString()))
        .andExpect(jsonPath("$.orderStatus").value("PENDING"))
        .andExpect(jsonPath("$.items[0].variantId").value(variantId.toString()))
        .andExpect(jsonPath("$.items[0].variantSize").value("M"))
        .andExpect(jsonPath("$.items[0].variantColor").value("Red"));

    verify(orderService, times(1)).createOrder(any(Map.class), any(), any());
  }

  @Test
  @WithMockUser
  void getMyOrders_ShouldIncludeVariantInformation() throws Exception {
    when(orderService.findOrdersOfCurrenUser()).thenReturn(List.of(orderDTO));

    mockMvc
        .perform(get("/api/v1/orders").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1))
        .andExpect(jsonPath("$[0].items[0].variantId").value(variantId.toString()))
        .andExpect(jsonPath("$[0].items[0].variantSize").value("M"))
        .andExpect(jsonPath("$[0].items[0].variantColor").value("Red"))
        .andExpect(jsonPath("$[0].items[0].productName").value("Producto Test"))
        .andExpect(jsonPath("$[0].items[0].quantity").value(2));

    verify(orderService, times(1)).findOrdersOfCurrenUser();
  }

  @Test
  @WithMockUser
  void findOrder_ShouldIncludeVariantDetails() throws Exception {
    when(orderService.findOrder(orderCode)).thenReturn(orderDTO);

    mockMvc
        .perform(
            get("/api/v1/orders/pick/{orderCode}", orderCode)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items[0].variantSize").value("M"))
        .andExpect(jsonPath("$.items[0].variantColor").value("Red"));

    verify(orderService, times(1)).findOrder(orderCode);
  }

  @Test
  @WithMockUser
  void createOrder_WithUnavailableVariant_ShouldThrowUnauthorized() throws Exception {
    Map<UUID, Integer> variantIdsWithQuantity = new HashMap<>();
    variantIdsWithQuantity.put(variantId, 2);

    when(orderService.createOrder(any(Map.class), any(), any()))
        .thenThrow(new UnauthorizedException("Variant not available"));

    mockMvc
        .perform(
            post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(variantIdsWithQuantity)))
        .andExpect(status().isForbidden());

    verify(orderService, times(1)).createOrder(any(Map.class), any(), any());
  }
}
