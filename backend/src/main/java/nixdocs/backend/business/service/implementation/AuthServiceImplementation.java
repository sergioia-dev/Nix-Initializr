package nixdocs.backend.business.service.implementation;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import nixdocs.backend.DTO.SignUpDTO;
import nixdocs.backend.business.repository.UserRepository;
import nixdocs.backend.business.service.AuthService;
import nixdocs.backend.persistance.model.User;

@Service
class AuthServiceImplementation implements AuthService {

  private UserRepository userRepository;
  private PasswordEncoder passwordEncoder;

  public AuthServiceImplementation(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
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

}
