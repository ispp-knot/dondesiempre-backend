package ispp.project.dondesiempre.modules.stores.controllers;

import ispp.project.dondesiempre.modules.stores.dtos.SocialNetworkDTO;
import ispp.project.dondesiempre.modules.stores.dtos.SocialNetworkUpdateDTO;
import ispp.project.dondesiempre.modules.stores.dtos.StoreSocialNetworkDTO;
import ispp.project.dondesiempre.modules.stores.models.StoreSocialNetwork;
import ispp.project.dondesiempre.modules.stores.services.StoreService;
import ispp.project.dondesiempre.modules.stores.services.StoreSocialNetworkService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StoreSocialNetworkController {

  private final StoreSocialNetworkService storeSocialNetworkService;
  private final StoreService storeService;

  @GetMapping("stores/{storeId}/social-networks")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<SocialNetworkDTO>> getByStore(@PathVariable UUID storeId) {

    storeService.findById(storeId); // valida que existe

    List<SocialNetworkDTO> socialNetworks =
        storeSocialNetworkService.findByStoreId(storeId).stream()
            .map(SocialNetworkDTO::new)
            .toList();

    return new ResponseEntity<>(socialNetworks, HttpStatus.OK);
  }

  @PostMapping("stores/{storeId}/social-networks")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<SocialNetworkDTO> create(
      @PathVariable UUID storeId, @RequestBody @Valid SocialNetworkDTO dto) {

    StoreSocialNetwork ssn = storeSocialNetworkService.addStoreSocialNetwork(storeId, dto);

    return new ResponseEntity<>(new SocialNetworkDTO(ssn), HttpStatus.CREATED);
  }

  @PutMapping("store-social-networks/{id}")
  public ResponseEntity<StoreSocialNetworkDTO> update(
      @PathVariable UUID id, @RequestBody @Valid SocialNetworkUpdateDTO dto) {

    StoreSocialNetwork ssn = storeSocialNetworkService.update(id, dto);

    return new ResponseEntity<>(new StoreSocialNetworkDTO(ssn), HttpStatus.OK);
  }

  @DeleteMapping("store-social-networks/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<String> delete(@PathVariable UUID id) {

    storeSocialNetworkService.delete(id);

    return new ResponseEntity<>("Social network successfully removed.", HttpStatus.OK);
  }
}
