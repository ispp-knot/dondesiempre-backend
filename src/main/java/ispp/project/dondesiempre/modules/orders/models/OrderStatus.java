package ispp.project.dondesiempre.modules.orders.models;

public enum OrderStatus {
  PENDING,
  REJECTED,
  CONFIRMED,
  PICKED,
  CANCELLED
}
// PENDING: El pedido ha sido creado pero no ha sido aceptado por la tienda.
// REJECTED: La tienda ha rechazado el pedido.
// CONFIRMED: La tienda ha aceptado el pedido y está en proceso de preparación.
// PICKED: El pedido ha sido recogido por el cliente.
// CANCELLED: El pedido ha sido cancelado por el cliente o la tienda.
