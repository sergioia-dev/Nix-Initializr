package nixdocs.backend.view.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
import nixdocs.backend.configuration.service.JwtService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthController(AuthService authService, JwtService jwtService, AuthenticationManager authenticationManager) {
    this.jwtService = jwtService;
    this.authService = authService;
    this.authenticationManager = authenticationManager;
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
  public ResponseEntity<?> signIn(@RequestBody SignInRequestDTO dto)
      throws JOSEException {

      if (dto.email() == null || dto.password()== null || dto.email().isEmpty() || dto.password().isEmpty()) {
      return ResponseEntity.badRequest()
          .body(new HTTPResponseDTO(HttpStatus.BAD_REQUEST.value(), "Bad Request", "Missing parameters"));
      }

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            dto.email(), // Now using email
            dto.password()));

    String email = authentication.getName(); // This will be the email

    String accessToken = jwtService.generateAccessToken(email); // Store email in token
    String refreshToken = jwtService.generateRefreshToken(email);

    jwtService.saveRefreshToken(email, refreshToken);
    return ResponseEntity.ok(new SignInResponseDTO(accessToken, refreshToken));
  }

}
