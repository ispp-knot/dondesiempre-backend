package ispp.project.dondesiempre.seed;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("seed")
@ConfigurationProperties(prefix = "seed")
@Getter
@Setter
public class SeedProperties {
  private long randomSeed = 42;
  private int storeCount = 5;
  private int productsPerStore = 6;
  private int socialNetworksPerStore = 2;
  private int outfitsPerStore = 2;
  private int clientCount = 10;
}
