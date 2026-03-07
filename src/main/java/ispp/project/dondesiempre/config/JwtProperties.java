package ispp.project.dondesiempre.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {

  /** Symmetric key for signing JWTs. Must be at least 32 bytes for HS256. */
  private String secret;

  /** Token lifetime in seconds. Defaults to 30 days. */
  private long duration = 2592000;

  /**
   * Whether to set the Secure flag on the JWT cookie, which restricts the browser to only sending
   * it over HTTPS. Must be false in local development (no TLS), and true in production. Controlled
   * via the JWT_SECURE_COOKIE env var, or forced to true by the prod Spring profile.
   */
  private boolean secureCookie = false;

  /**
   * SameSite attribute for the JWT cookie. "Lax" in development (allows normal navigation); "None"
   * in production (required for cross-site requests, must be paired with Secure=true). Controlled
   * via the JWT_SAME_SITE env var, or forced to "None" by the prod Spring profile.
   */
  private String sameSite = "Lax";
}
