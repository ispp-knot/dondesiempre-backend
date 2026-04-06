package ispp.project.dondesiempre.modules.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class LimitExceededException extends RuntimeException {
  public LimitExceededException(String message) {
    super(message);
  }

  public LimitExceededException() {
      super("Limit for your plan exceeded.");
  }
}
