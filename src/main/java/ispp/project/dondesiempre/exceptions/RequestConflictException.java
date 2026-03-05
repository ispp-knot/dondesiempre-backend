package ispp.project.dondesiempre.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RequestConflictException extends RuntimeException {
  public RequestConflictException(String message) {
    super(message);
  }
}
