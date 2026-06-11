package nixdocs.backend.business.service;

import nixdocs.backend.DTO.SignUpDTO;

public interface AuthService {

  public boolean createLocalAccount(SignUpDTO dto);

}
