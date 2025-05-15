package stquokka.codeStudy.domain.auth.service;

import stquokka.codeStudy.api.auth.request.TokenRefreshRequest;
import stquokka.codeStudy.api.auth.response.AuthResponse;
import stquokka.codeStudy.domain.auth.command.LoginCommand;

public interface AuthService {
    AuthResponse login(LoginCommand command);
    AuthResponse refresh(TokenRefreshRequest refreshToken);
    void logout();
}
