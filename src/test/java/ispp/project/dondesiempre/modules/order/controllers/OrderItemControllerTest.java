package ispp.project.dondesiempre.modules.order.controllers;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ispp.project.dondesiempre.config.GlobalExceptionHandler;
import ispp.project.dondesiempre.modules.orders.controllers.OrderItemController;
import ispp.project.dondesiempre.modules.orders.dtos.OrderDTO.OrderItemDTO;
import ispp.project.dondesiempre.modules.orders.services.OrderItemService;
import java.util.List;
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
    controllers = OrderItemController.class,
    excludeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {GlobalExceptionHandler.class}))
class OrderItemControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private OrderItemService orderItemService;

  private UUID itemId;
  private UUID orderId;
  private UUID productId;
  private OrderItemDTO itemDTO;

  @BeforeEach
  void setUp() {
    itemId = UUID.randomUUID();
    orderId = UUID.randomUUID();
    productId = UUID.randomUUID();

    itemDTO =
        OrderItemDTO.builder()
            .productId(productId)
            .productName("Producto Test")
            .quantity(2)
            .priceAtPurchase(100)
            .subtotal(200)
            .build();
  }

  @Test
  @WithMockUser
  void getAllOrderItems_shouldReturnOk() throws Exception {
    when(orderItemService.findAllOrderItems()).thenReturn(List.of(itemDTO));

    mockMvc
        .perform(get("/api/v1/order-items").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1))
        .andExpect(jsonPath("$[0].productName").value("Producto Test"));

    verify(orderItemService, times(1)).findAllOrderItems();
  }

  @Test
  @WithMockUser
  void getItemsByOrderId_shouldReturnOk() throws Exception {
    when(orderItemService.findItemsByOrderId(orderId)).thenReturn(List.of(itemDTO));

    mockMvc
        .perform(
            get("/api/v1/order-items/order/{orderId}", orderId)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1))
        .andExpect(jsonPath("$[0].productId").value(productId.toString()));

    verify(orderItemService, times(1)).findItemsByOrderId(orderId);
  }

  @Test
  @WithMockUser
  void getItemsByProductId_shouldReturnOk() throws Exception {
    when(orderItemService.findItemsByProductId(productId)).thenReturn(List.of(itemDTO));

    mockMvc
        .perform(
            get("/api/v1/order-items/product/{productId}", productId)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1))
        .andExpect(jsonPath("$[0].quantity").value(2));

    verify(orderItemService, times(1)).findItemsByProductId(productId);
  }

  @Test
  @WithMockUser
  void deleteOrderItem_shouldReturnNoContent() throws Exception {
    doNothing().when(orderItemService).deleteOrderItem(itemId);

    mockMvc
        .perform(delete("/api/v1/order-items/{id}", itemId).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    verify(orderItemService, times(1)).deleteOrderItem(itemId);
  }
}
