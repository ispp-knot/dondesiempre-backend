package ispp.project.dondesiempre.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import ispp.project.dondesiempre.config.JwtProperties;
import ispp.project.dondesiempre.modules.auth.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

  private static final String SECRET = "test-secret-must-be-32-bytes-or-this-fails";

  @Mock private JwtProperties jwtProperties;
  @InjectMocks private JwtService jwtService;

  @BeforeEach
  void setUp() {
    when(jwtProperties.getSecret()).thenReturn(SECRET);
  }

  @Test
  void generateToken_shouldReturnThreePartJwt() {
    when(jwtProperties.getDuration()).thenReturn(3600L);
    String token = jwtService.generateToken("user@test.com");
    assertNotNull(token);
    assertEquals(3, token.split("\\.").length);
  }

  @Test
  void extractEmail_shouldReturnEmailEmbeddedInToken() {
    when(jwtProperties.getDuration()).thenReturn(3600L);
    String token = jwtService.generateToken("user@test.com");
    assertEquals("user@test.com", jwtService.extractEmail(token));
  }

  @Test
  void isTokenValid_shouldReturnTrue_forFreshToken() {
    when(jwtProperties.getDuration()).thenReturn(3600L);
    String token = jwtService.generateToken("user@test.com");
    assertTrue(jwtService.isTokenValid(token));
  }

  @Test
  void isTokenValid_shouldReturnFalse_forExpiredToken() {
    when(jwtProperties.getDuration()).thenReturn(-3600L);
    String token = jwtService.generateToken("user@test.com");
    assertFalse(jwtService.isTokenValid(token));
  }

  @Test
  void isTokenValid_shouldReturnFalse_forTamperedToken() {
    when(jwtProperties.getDuration()).thenReturn(3600L);
    String token = jwtService.generateToken("user@test.com");
    assertFalse(jwtService.isTokenValid(token + "tampered"));
  }

  @Test
  void isTokenValid_shouldReturnFalse_forGarbage() {
    assertFalse(jwtService.isTokenValid("not-a-jwt-at-all"));
  }
}
