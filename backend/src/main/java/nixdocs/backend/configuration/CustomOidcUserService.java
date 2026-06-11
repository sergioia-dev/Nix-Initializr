package nixdocs.backend.configuration;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import nixdocs.backend.DTO.enums.ProviderTypes;
import nixdocs.backend.business.repository.UserRepository;
import nixdocs.backend.persistance.model.User;

@Service
public class CustomOidcUserService extends OidcUserService {

  private final UserRepository userRepository;

  public CustomOidcUserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
    // Delegate to default implementation to get the standard OidcUser
    OidcUser oidcUser = super.loadUser(userRequest);

    // Extract user info from OIDC
    String email = oidcUser.getEmail();
    String name = oidcUser.getFullName();
    if (name == null) {
      name = oidcUser.getName(); // fallback
    }
    String registrationId = userRequest.getClientRegistration().getRegistrationId();

    User user;

    // Check if user already exists in local database
    var existing = userRepository.findByEmail(email);
    if (existing.isPresent()) {
      user = existing.get();
      if (ProviderTypes.LOCAL.equals(user.getProvider())) {
        throw new OAuth2AuthenticationException(
            "An account with this email already exists. Please sign in with your email and password.");
      }
      // Update name if changed
      if (!name.equals(user.getUsername())) {
        user.setUsername(name);
        user = userRepository.save(user);
      }
    } else {
      // Create new local user
      user = new User();
      user.setEmail(email);
      user.setUsername(name);
      user.setProvider(ProviderTypes.valueOf(registrationId));
      // No password for OIDC users
      user.setPassword(null); // or a placeholder like ""

      user = userRepository.save(user);


    // Convert local authorities to Spring Security GrantedAuthority
    // Build and return your CustomUserDetails (which implements OidcUser)
    return new CustomUserDetailsService()
}
