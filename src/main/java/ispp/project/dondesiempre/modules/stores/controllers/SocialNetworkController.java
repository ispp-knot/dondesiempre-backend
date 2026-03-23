package ispp.project.dondesiempre.modules.stores.controllers;

import ispp.project.dondesiempre.modules.stores.services.SocialNetworkService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SocialNetworkController {
  private final SocialNetworkService socialNetworkService;

  @GetMapping("/social-networks/names")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<String>> getSocialNetworkNames() {
    return new ResponseEntity<>(socialNetworkService.findAllNames(), HttpStatus.OK);
  }
}
