package ispp.project.dondesiempre.modules.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class StoreNotVerifiedException extends RuntimeException {

  public StoreNotVerifiedException() {
    super("La tienda debe estar verificada para recibir pagos.");
  }

  public StoreNotVerifiedException(String message) {
    super(message);
  }
}
