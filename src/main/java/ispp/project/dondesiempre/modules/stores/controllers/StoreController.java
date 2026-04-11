package ispp.project.dondesiempre.modules.stores.controllers;

import ispp.project.dondesiempre.modules.payment.dto.AccountStatusDTO;
import ispp.project.dondesiempre.modules.payment.dto.StripeDashboardLinkDTO;
import ispp.project.dondesiempre.modules.payment.dto.StripeOnBoardingLinkDTO;
import ispp.project.dondesiempre.modules.payment.services.PaymentService;
import ispp.project.dondesiempre.modules.stores.dtos.StoreDTO;
import ispp.project.dondesiempre.modules.stores.dtos.StoreUpdateDTO;
import ispp.project.dondesiempre.modules.stores.dtos.StoreUpdateLocationDTO;
import ispp.project.dondesiempre.modules.stores.services.StoreService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StoreController {
  private final StoreService storeService;
  private final PaymentService paymentService;

  @GetMapping("/stores")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<StoreDTO>> searchStores(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Double lat,
      @RequestParam(required = false) Double lon) {
    return new ResponseEntity<>(
        storeService.searchStores(name, lat, lon).stream()
            .map(store -> storeService.toDTO(store, lat, lon))
            .toList(),
        HttpStatus.OK);
  }

  @GetMapping("/stores/map")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<StoreDTO>> getStoresInMap(
      @RequestParam Double minLon,
      @RequestParam Double minLat,
      @RequestParam Double maxLon,
      @RequestParam Double maxLat) {
    return new ResponseEntity<>(
        storeService.findStoresInBoundingBoxAsDTO(minLon, minLat, maxLon, maxLat), HttpStatus.OK);
  }

  @GetMapping("/stores/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<StoreDTO> getStoreById(@PathVariable UUID id) {
    return new ResponseEntity<>(storeService.toDTO(storeService.findById(id)), HttpStatus.OK);
  }

  @PutMapping("/stores/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<StoreDTO> updateStore(
      @PathVariable("id") UUID id, @RequestBody @Valid StoreUpdateDTO dto) {
    return new ResponseEntity<>(storeService.updateStore(id, dto), HttpStatus.OK);
  }

  @PutMapping("/stores/{id}/location")
  public ResponseEntity<StoreDTO> updateStoreLocation(
      @PathVariable UUID id, @RequestBody @Valid StoreUpdateLocationDTO dto) {
    return new ResponseEntity<>(
        storeService.updateLocation(id, dto.getLongitude(), dto.getLatitude()), HttpStatus.OK);
  }

  @GetMapping("/stores/{id}/stripe/status")
  public ResponseEntity<AccountStatusDTO> checkStoreStripeAccountStatus(@PathVariable UUID id) {
    boolean verificationStatus =
        paymentService.checkAccountIsVerifiedForPayments(storeService.findById(id));
    return new ResponseEntity<>(new AccountStatusDTO(verificationStatus), HttpStatus.OK);
  }

  @GetMapping("/stores/{id}/stripe/onboarding")
  public ResponseEntity<StripeOnBoardingLinkDTO> getOnBoardingLink(@PathVariable UUID id) {
    String link = paymentService.getStripeOnboardingLink(storeService.findById(id));
    return new ResponseEntity<>(new StripeOnBoardingLinkDTO(link), HttpStatus.OK);
  }

  @GetMapping("/stores/{id}/stripe/dashboard")
  public ResponseEntity<StripeDashboardLinkDTO> getStripeDashboardLink(@PathVariable UUID id) {
    String link = paymentService.getStripeDashboardLink(storeService.findById(id));
    return new ResponseEntity<>(new StripeDashboardLinkDTO(link), HttpStatus.OK);
  }
}
