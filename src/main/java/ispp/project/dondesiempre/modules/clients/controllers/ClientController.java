package ispp.project.dondesiempre.modules.clients.controllers;

import ispp.project.dondesiempre.modules.clients.dtos.ClientDTO;
import ispp.project.dondesiempre.modules.clients.dtos.ClientUpdateDTO;
import ispp.project.dondesiempre.modules.clients.services.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ClientController {
  private final ClientService clientService;

  @PutMapping("clients")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<ClientDTO> updateClient(@RequestBody @Valid ClientUpdateDTO dto) {
    return new ResponseEntity<>(clientService.updateClient(dto), HttpStatus.OK);
  }
}
