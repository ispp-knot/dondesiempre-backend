package ispp.project.dondesiempre.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidBoundingBoxException extends RuntimeException {
  public InvalidBoundingBoxException() {
    super("Invalid request.");
  }

  public InvalidBoundingBoxException(String message) {
    super(message);
  }
}
