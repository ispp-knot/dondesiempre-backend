package ispp.project.dondesiempre.modules.payment.controllers;

import ispp.project.dondesiempre.modules.payment.dto.PaymentInitDTO;
import ispp.project.dondesiempre.modules.payment.services.PaymentService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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

  @GetMapping("/checkout/{orderId}")
  public ResponseEntity<PaymentInitDTO> initiateOrderPayment(
      @PathVariable("orderId") UUID orderId) {

    String sessionUrl = paymentService.initiateOrderPayment(orderId);

    return ResponseEntity.ok(new PaymentInitDTO(sessionUrl));
  }
}
