package ispp.project.dondesiempre.modules.order.controllers;

import static org.mockito.ArgumentMatchers.any;
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
import ispp.project.dondesiempre.modules.orders.dtos.OrderDTO;
import ispp.project.dondesiempre.modules.orders.models.OrderStatus;
import ispp.project.dondesiempre.modules.orders.services.OrderService;
import ispp.project.dondesiempre.modules.orders.controllers.OrderController;
import ispp.project.dondesiempre.modules.products.models.Product;
import ispp.project.dondesiempre.modules.products.services.ProductService;
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
  private OrderDTO orderDTO;
  private Product product;

  @BeforeEach
  void setUp() {
    orderId = UUID.randomUUID();
    productId = UUID.randomUUID();

    orderDTO =
        OrderDTO.builder()
            .id(orderId)
            .orderCode("ABCD-1234-EFGH")
            .orderStatus(OrderStatus.PENDING)
            .totalPrice(1500)
            .build();

    product = new Product();
    product.setId(productId);
    product.setName("Camiseta de prueba");
    product.setPriceInCents(1500);
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
        .andExpect(jsonPath("$[0].orderCode").value("ABCD-1234-EFGH"));

    verify(orderService, times(1)).findOrdersOfCurrenUser();
  }

  @Test
  @WithMockUser
  void createOrder_shouldReturnCreated() throws Exception {
    Map<UUID, Integer> payload = Map.of(productId, 1);

    when(productService.getProductById(productId)).thenReturn(product);
    when(orderService.createOrder(any(Map.class))).thenReturn(orderDTO);

    mockMvc
        .perform(
            post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(orderId.toString()))
        .andExpect(jsonPath("$.orderCode").value("ABCD-1234-EFGH"));

    verify(productService, times(1)).getProductById(productId);
    verify(orderService, times(1)).createOrder(any(Map.class));
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
            patch("/api/v1/orders/{orderId}/pick", orderId)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    verify(orderService, times(1)).pickOrder(orderId);
  }
}