package ispp.project.dondesiempre.modules.auth.controllers;

import ispp.project.dondesiempre.config.JwtProperties;
import ispp.project.dondesiempre.modules.auth.dtos.LoginRequestDTO;
import ispp.project.dondesiempre.modules.auth.dtos.UserResponseDTO;
import ispp.project.dondesiempre.modules.auth.models.User;
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

  @PostMapping("/login")
  public ResponseEntity<Void> logIn(
      @RequestBody LoginRequestDTO dto, HttpServletResponse response) {
    String token = authService.logIn(dto.email(), dto.password());
    ResponseCookie cookie = ResponseCookie.from("token", token)
        .httpOnly(true)
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
