package nixdocs.backend.business.service;

import com.nimbusds.jose.JOSEException;

import nixdocs.backend.DTO.SignInRequestDTO;
import nixdocs.backend.DTO.SignInResponseDTO;
import nixdocs.backend.DTO.SignUpDTO;

public interface AuthService {

  public boolean createLocalAccount(SignUpDTO dto);

  public SignInResponseDTO signIn(SignInRequestDTO dto) throws JOSEException;

  public SignInResponseDTO refreshAccessToken(String refreshToken);

  public String checkStatus(String accessToken);

  public SignInResponseDTO handleOAuth2Login(String email) throws JOSEException;

}
