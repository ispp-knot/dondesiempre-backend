package ispp.project.dondesiempre.config;

import com.cloudinary.Cloudinary;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CloudinaryConfig {

  private final CloudinaryProperties properties;

  @Bean
  public Cloudinary cloudinary() {
    String url = properties.getUrl();
    if (url == null || url.isBlank()) {
      return new Cloudinary(Map.of());
    }
    return new Cloudinary(url);
  }
}
