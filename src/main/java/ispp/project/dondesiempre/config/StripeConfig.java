package ispp.project.dondesiempre.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication
public class StripeConfig {

  @Value("${stripe.key.secret}")
  private String stripeSecretKey;

  @PostConstruct
  public void init() {
    Stripe.apiKey = stripeSecretKey;
  }
}
