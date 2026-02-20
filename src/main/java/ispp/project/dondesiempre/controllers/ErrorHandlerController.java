package ispp.project.dondesiempre.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ErrorHandlerController implements ErrorController {

  @GetMapping("/error")
  public String customError() {
    throw new ResponseStatusException(HttpStatusCode.valueOf(404));
  }
}
