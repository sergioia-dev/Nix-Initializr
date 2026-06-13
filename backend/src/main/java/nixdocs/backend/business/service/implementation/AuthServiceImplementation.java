package nixdocs.backend.business.service.implementation;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

import nixdocs.backend.DTO.SignInRequestDTO;
import nixdocs.backend.DTO.SignInResponseDTO;
import nixdocs.backend.DTO.SignUpDTO;
import nixdocs.backend.business.repository.UserRepository;
import nixdocs.backend.business.service.AuthService;
import nixdocs.backend.configuration.service.JwtService;
import nixdocs.backend.persistance.model.User;

@Service
class AuthServiceImplementation implements AuthService {

  private UserRepository userRepository;
  private PasswordEncoder passwordEncoder;
  private AuthenticationManager authenticationManager;
  private JwtService jwtService;

  public AuthServiceImplementation(UserRepository userRepository, PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager, JwtService jwtService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
  }

  @Override
  public boolean createLocalAccount(SignUpDTO dto) {
    if (userRepository.existsByEmail(dto.email())) {
      return false;
    }

    User user = new User();

    user.setUsername(dto.username());
    user.setEmail(dto.email());
    user.setPassword(passwordEncoder.encode(dto.password()));
    userRepository.save(user);

    return true;
  }

  @Override
  public SignInResponseDTO signIn(SignInRequestDTO dto) throws JOSEException {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(dto.email(), dto.password()));

    String email = authentication.getName();

    String accessToken = jwtService.generateAccessToken(email);
    String refreshToken = jwtService.generateRefreshToken(email);

    jwtService.saveRefreshToken(email, refreshToken);
    return new SignInResponseDTO(accessToken, refreshToken);
  }

  @Override
  public SignInResponseDTO refreshAccessToken(String refreshToken) {
    try {
      String email = jwtService.extractSubject(refreshToken);
      String tokenType = jwtService.extractTokenType(refreshToken);

      if (!"REFRESH".equals(tokenType)) {
        return null;
      }

      if (!jwtService.validateRefreshToken(email, refreshToken)) {
        return null;
      }

      String newAccessToken = jwtService.generateAccessToken(email);
      String newRefreshToken = jwtService.generateRefreshToken(email);
      jwtService.saveRefreshToken(email, newRefreshToken);
      return new SignInResponseDTO(newAccessToken, newRefreshToken);

    } catch (ParseException | JOSEException e) {
      return null;
    }
  }

}
