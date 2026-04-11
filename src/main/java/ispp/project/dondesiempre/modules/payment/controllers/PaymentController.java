package ispp.project.dondesiempre.modules.payment.controllers;

import ispp.project.dondesiempre.modules.common.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.modules.orders.services.OrderService;
import ispp.project.dondesiempre.modules.payment.dto.AccountStatusDTO;
import ispp.project.dondesiempre.modules.payment.dto.PaymentInitDTO;
import ispp.project.dondesiempre.modules.payment.services.PaymentService;
import ispp.project.dondesiempre.modules.stores.models.Store;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;
  private final OrderService orderService;

  @GetMapping("/checkout/{orderId}")
  public ResponseEntity<PaymentInitDTO> initiateOrderPayment(
      @PathVariable("orderId") UUID orderId) {

    String sessionUrl = paymentService.initiateOrderPayment(orderId);

    return ResponseEntity.ok(new PaymentInitDTO(sessionUrl));
  }

  @GetMapping("/checkout/{orderId}/stripe/status")
  public ResponseEntity<AccountStatusDTO> getStoreVerificationStatus(
      @PathVariable("orderId") UUID orderId) {
    Store store =
        orderService
            .findById(orderId)
            .getStore()
            .orElseThrow(() -> new InvalidRequestException("El pedido no tiene productos."));
    boolean isVerified = paymentService.checkAccountIsVerifiedForPayments(store);
    return new ResponseEntity<>(new AccountStatusDTO(isVerified), HttpStatus.OK);
  }
}
