package ispp.project.dondesiempre.controllers.auth;

import ispp.project.dondesiempre.config.JwtProperties;
import ispp.project.dondesiempre.controllers.auth.dto.LoginRequestDTO;
import ispp.project.dondesiempre.controllers.auth.dto.UserResponseDTO;
import ispp.project.dondesiempre.models.User;
import ispp.project.dondesiempre.services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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
  private final JwtProperties jwtProperties;

  @PostMapping("/logIn")
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
}
