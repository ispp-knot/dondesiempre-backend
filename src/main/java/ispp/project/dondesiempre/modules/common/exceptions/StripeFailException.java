package ispp.project.dondesiempre.modules.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class StripeFailException extends RuntimeException {
  public StripeFailException() {
    super("Stripe internal error.");
  }

  public StripeFailException(String message) {
    super(message);
  }
}
