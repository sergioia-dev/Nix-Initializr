package nixdocs.backend.view.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nimbusds.jose.JOSEException;
import nixdocs.backend.DTO.HTTPResponseDTO;
import nixdocs.backend.DTO.SignInRequestDTO;
import nixdocs.backend.DTO.SignInResponseDTO;
import nixdocs.backend.DTO.SignUpDTO;
import nixdocs.backend.business.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Value("${JWT_ACCESS_TOKEN_EXPIRATION}")
  private Long accessTokenExpiration;

  @Value("${JWT_REFRESH_TOKEN_EXPIRATION}")
  private Long refreshTokenExpiration;

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping(path = "/signup", version = "1")
  public ResponseEntity<HTTPResponseDTO> createLocalAccount(@RequestBody SignUpDTO dto) {
    if (dto.email() == null || dto.username() == null || dto.password() == null || dto.email().isEmpty()
        || dto.username().isEmpty() || dto.password().isEmpty()) {
      return ResponseEntity.badRequest()
          .body(new HTTPResponseDTO(HttpStatus.BAD_REQUEST.value(), "Bad Request", "Missing parameters"));
    }
    authService.createLocalAccount(dto);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PostMapping(path = "/signin", version = "1")
  public ResponseEntity<?> signIn(@RequestBody SignInRequestDTO dto) throws JOSEException {
    if (dto.email() == null || dto.password() == null || dto.email().isEmpty() || dto.password().isEmpty()) {
      return ResponseEntity.badRequest()
          .body(new HTTPResponseDTO(HttpStatus.BAD_REQUEST.value(), "Bad Request", "Missing parameters"));
    }
    SignInResponseDTO tokens = authService.signIn(dto);
    return ResponseEntity.ok()
        .headers(buildCookieHeaders(tokens.accessToken(), tokens.refreshToken()))
        .build();
  }

  @PostMapping(path = "/refresh", version = "1")
  public ResponseEntity<?> refreshAccessToken(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
    if (refreshToken == null || refreshToken.isEmpty()) {
      return ResponseEntity.badRequest()
          .body(new HTTPResponseDTO(HttpStatus.BAD_REQUEST.value(), "Bad Request", "Missing refresh token"));
    }
    SignInResponseDTO tokens = authService.refreshAccessToken(refreshToken);
    if (tokens == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new HTTPResponseDTO(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", "Invalid or expired refresh token"));
    }
    return ResponseEntity.ok()
        .headers(buildCookieHeaders(tokens.accessToken(), tokens.refreshToken()))
        .build();
  }

  private HttpHeaders buildCookieHeaders(String accessToken, String refreshToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.SET_COOKIE,
        ResponseCookie.from("accessToken", accessToken)
            .httpOnly(true)
            .secure(true)
            .sameSite("Strict")
            .path("/")
            .maxAge(accessTokenExpiration / 1000)
            .build()
            .toString());
    headers.add(HttpHeaders.SET_COOKIE,
        ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)
            .secure(true)
            .sameSite("Strict")
            .path("/api/auth/refresh")
            .maxAge(refreshTokenExpiration / 1000)
            .build()
            .toString());
    return headers;
  }
}
