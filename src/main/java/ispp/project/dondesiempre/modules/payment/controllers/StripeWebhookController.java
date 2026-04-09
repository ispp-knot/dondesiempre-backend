package ispp.project.dondesiempre.modules.payment.controllers;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import ispp.project.dondesiempre.modules.common.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.modules.payment.services.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/webhook")
public class StripeWebhookController {

  private final PaymentService paymentService;
  private final String endpointSecret;

  public StripeWebhookController(
      PaymentService paymentService, @Value("${stripe.webhook.secret}") String endpointSecret) {
    this.paymentService = paymentService;
    this.endpointSecret = endpointSecret;
  }

  @PostMapping
  public ResponseEntity<Void> capturePayments(
      @RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader)
      throws StripeException {
    try {
      Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
      paymentService.capturePayment(event);
      return ResponseEntity.noContent().build();
    } catch (SignatureVerificationException e) {
      throw new InvalidRequestException("Invalid webhook signature");
    }
  }
}
