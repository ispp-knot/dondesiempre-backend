package ispp.project.dondesiempre.controllers.exceptions;

import ispp.project.dondesiempre.exceptions.InvalidBoundingBoxException;
import ispp.project.dondesiempre.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.exceptions.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<String> handleResourceNotFoundException(
      ResourceNotFoundException exception, WebRequest request) {
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(InvalidRequestException.class)
  public ResponseEntity<String> handleInvalidRequestException(
      InvalidRequestException exception, WebRequest request) {
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(InvalidBoundingBoxException.class)
  public ResponseEntity<String> handleInvalidBoundingBoxException(
      InvalidBoundingBoxException exception, WebRequest request) {
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<String> handleUnauthorizedException(
      UnauthorizedException exception, WebRequest request) {
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleException(Exception exception, WebRequest request) {
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
