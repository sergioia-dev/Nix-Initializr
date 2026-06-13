package nixdocs.backend.configuration;

import java.io.IOException;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nixdocs.backend.configuration.service.JwtService;
import tools.jackson.databind.ObjectMapper;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  private final JwtService jwtService;
  private final ObjectMapper objectMapper;

  public OAuth2AuthenticationSuccessHandler(JwtService jwtService, ObjectMapper objectMapper) {
    this.jwtService = jwtService;
    this.objectMapper = objectMapper;
  }

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    String email = oAuth2User.getAttribute("email");

    try {
      String accessToken = jwtService.generateAccessToken(email);
      String refreshToken = jwtService.generateRefreshToken(email);

      jwtService.saveRefreshToken(email, refreshToken);

      response.setContentType("application/json");
      response.getWriter().write(objectMapper.writeValueAsString(Map.of(
          "access_token", accessToken,
          "refresh_token", refreshToken
      )));
    } catch (JOSEException e) {
      throw new ServletException("Failed to generate JWT tokens", e);
    }
  }
}
