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
  private String secret = "dev-secret-must-be-changed-in-production!!";

  /** Token lifetime in seconds. Defaults to 30 days. */
  private long duration = 2592000;
}
