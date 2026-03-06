package ispp.project.dondesiempre.security;

import ispp.project.dondesiempre.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  public JwtAuthFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      Arrays.stream(cookies)
          .filter(c -> "token".equals(c.getName()))
          .findFirst()
          .ifPresent(
              cookie -> {
                String token = cookie.getValue();
                if (jwtService.isTokenValid(token)) {
                  String email = jwtService.extractEmail(token);
                  UsernamePasswordAuthenticationToken auth =
                      new UsernamePasswordAuthenticationToken(email, null, List.of());
                  SecurityContextHolder.getContext().setAuthentication(auth);
                }
              });
    }
    chain.doFilter(request, response);
  }
}
