package ispp.project.dondesiempre.controllers.auth;

import ispp.project.dondesiempre.config.JwtProperties;
import ispp.project.dondesiempre.controllers.auth.dto.LoginRequestDTO;
import ispp.project.dondesiempre.controllers.auth.dto.RegisterClientDTO;
import ispp.project.dondesiempre.controllers.auth.dto.RegisterStoreDTO;
import ispp.project.dondesiempre.controllers.auth.dto.UserResponseDTO;
import ispp.project.dondesiempre.models.User;
import ispp.project.dondesiempre.models.clients.ClientDTO;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.models.stores.dto.StoreDTO;
import ispp.project.dondesiempre.services.AuthService;
import ispp.project.dondesiempre.services.UserService;
import ispp.project.dondesiempre.services.stores.StoreService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final UserService userService;
  private final StoreService storeService;
  private final JwtProperties jwtProperties;

  @PostMapping("/login")
  public ResponseEntity<Void> logIn(
      @RequestBody LoginRequestDTO dto, HttpServletResponse response) {
    String token = authService.logIn(dto.email(), dto.password());
    ResponseCookie cookie =
        ResponseCookie.from("token", token)
            // Prevent JavaScript from reading the cookie (XSS mitigation).
            .httpOnly(true)
            // Only send the cookie over HTTPS. Disabled in dev (no TLS); enabled in prod
            // via jwt.secure-cookie=true (set in application-prod.yaml or JWT_SECURE_COOKIE
            // env).
            .secure(jwtProperties.isSecureCookie())
            .sameSite(jwtProperties.getSameSite())
            .path("/")
            .maxAge(jwtProperties.getDuration())
            .build();
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    return ResponseEntity.ok().build();
  }

  @GetMapping("/me")
  public ResponseEntity<UserResponseDTO> me() {
    User user = authService.getCurrentUser();
    return ResponseEntity.ok(new UserResponseDTO(user.getId(), user.getEmail()));
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
}
