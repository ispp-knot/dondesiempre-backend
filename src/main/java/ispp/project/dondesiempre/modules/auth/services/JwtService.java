package ispp.project.dondesiempre.modules.auth.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import ispp.project.dondesiempre.config.JwtProperties;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

  private final JwtProperties jwtProperties;

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
  }

  public String generateToken(String email, UUID storeId, UUID clientId) {
    long nowMillis = System.currentTimeMillis();
    long expMillis = nowMillis + jwtProperties.getDuration() * 1000;
    var builder =
        Jwts.builder().subject(email).issuedAt(new Date(nowMillis)).expiration(new Date(expMillis));
    if (storeId != null) builder.claim("storeId", storeId.toString());
    if (clientId != null) builder.claim("clientId", clientId.toString());
    return builder.signWith(getSigningKey()).compact();
  }

  public String extractEmail(String token) {
    return parseClaims(token).getSubject();
  }

  public Instant getExpiresAt(String token) {
    return parseClaims(token).getExpiration().toInstant();
  }

  public boolean isTokenValid(String token) {
    try {
      Claims claims = parseClaims(token);
      return claims.getExpiration().after(new Date());
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  private Claims parseClaims(String token) {
    return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
  }
}
