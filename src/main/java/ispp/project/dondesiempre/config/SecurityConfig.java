package ispp.project.dondesiempre.config;

import ispp.project.dondesiempre.security.JwtAuthFilter;
import ispp.project.dondesiempre.services.JwtService;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnWebApplication
public class SecurityConfig implements WebMvcConfigurer {

  Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

  @Autowired(required = false)
  private JwtService jwtService;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth
                    // Login is public
                    .requestMatchers(HttpMethod.POST, "/api/v1/auth/login")
                    .permitAll()
                    // Store reads are public
                    .requestMatchers(HttpMethod.GET, "/api/v1/stores", "/api/v1/stores/all")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/stores/*/outfits")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/stores/*/promotions")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/stores/*")
                    .permitAll()
                    // Product reads are public
                    .requestMatchers(
                        HttpMethod.GET, "/api/v1/products", "/api/v1/products/discounted")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/products/*")
                    .permitAll()
                    // Storefront reads are public
                    .requestMatchers(HttpMethod.GET, "/api/v1/storefronts/*/products")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/storefronts/*/outfits")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/storefronts/*")
                    .permitAll()
                    // Outfit reads are public
                    .requestMatchers(HttpMethod.GET, "/api/v1/outfits/*")
                    .permitAll()
                    // Promotion reads are public
                    .requestMatchers(HttpMethod.GET, "/api/v1/promotions", "/api/v1/promotions/*")
                    .permitAll()
                    // Health check and error handler
                    .requestMatchers(HttpMethod.GET, "/api/v1/health")
                    .permitAll()
                    .requestMatchers("/error")
                    .permitAll()
                    // Swagger UI (dev profile only)
                    .requestMatchers("/api/v1/swagger-ui/**", "/api/v1/api-docs/**")
                    .permitAll()
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**")
                    .permitAll()
                    // Everything else requires authentication
                    .anyRequest()
                    .authenticated());

    if (jwtService != null) {
      http.addFilterBefore(
          new JwtAuthFilter(jwtService), UsernamePasswordAuthenticationFilter.class);
    }

    return http.build();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    logger.info("Configuring CORS");

    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("*"));
    configuration.setAllowedMethods(Arrays.asList("*"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
