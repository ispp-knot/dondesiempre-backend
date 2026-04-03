package ispp.project.dondesiempre.modules.auth.controllers;

import ispp.project.dondesiempre.modules.auth.dtos.LoginRequestDTO;
import ispp.project.dondesiempre.modules.auth.dtos.LoginResponseDTO;
import ispp.project.dondesiempre.modules.auth.dtos.RegisterClientDTO;
import ispp.project.dondesiempre.modules.auth.dtos.RegisterStoreDTO;
import ispp.project.dondesiempre.modules.auth.dtos.UserResponseDTO;
import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.auth.services.AuthService;
import ispp.project.dondesiempre.modules.auth.services.JwtService;
import ispp.project.dondesiempre.modules.auth.services.UserService;
import ispp.project.dondesiempre.modules.clients.dtos.ClientDTO;
import ispp.project.dondesiempre.modules.stores.dtos.StoreDTO;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.services.StoreService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final UserService userService;
  private final StoreService storeService;
  private final JwtService jwtService;

  @PostMapping("/login")
  public ResponseEntity<LoginResponseDTO> logIn(@RequestBody LoginRequestDTO dto) {
    User user = authService.logIn(dto.email(), dto.password());
    UUID storeId = authService.getStoreId(user);
    UUID clientId = authService.getClientId(user);
    String token = jwtService.generateToken(user.getEmail(), storeId, clientId);
    return ResponseEntity.ok(
        new LoginResponseDTO(authService.buildUserResponse(user, token), token));
  }

  @GetMapping("/me")
  public ResponseEntity<UserResponseDTO> me(@RequestHeader("Authorization") String authHeader) {
    String token = authHeader.substring(7);
    User user = authService.getCurrentUser();
    return ResponseEntity.ok(authService.buildUserResponse(user, token));
  }

  @PostMapping("/register/store")
  public ResponseEntity<StoreDTO> registerStore(@Valid @RequestBody RegisterStoreDTO dto) {
    Store store = userService.registerStore(dto);
    return new ResponseEntity<>(storeService.toDTO(store), HttpStatus.CREATED);
  }

  @PostMapping("/register/client")
  public ResponseEntity<ClientDTO> registerClient(@Valid @RequestBody RegisterClientDTO dto) {
    return new ResponseEntity<>(userService.registerClient(dto), HttpStatus.CREATED);
  }

  @PatchMapping("/password")
  public ResponseEntity<Void> changePassword(
      @RequestParam String oldPassword, @RequestParam String newPassword) {
    userService.changePassword(oldPassword, newPassword);
    return ResponseEntity.accepted().build();
  }
}
