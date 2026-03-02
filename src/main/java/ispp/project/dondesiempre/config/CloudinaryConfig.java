package ispp.project.dondesiempre.config;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CloudinaryConfig {

  private final CloudinaryProperties properties;

  @Bean
  public Cloudinary cloudinary() {
    return new Cloudinary(properties.getUrl());
  }
}
