package stquokka.codeStudy.domain.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/**
 * OAuth2 인증 실패 시 처리를 담당하는 핸들러
 */
@Slf4j
@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final String REDIRECT_URI = "http://localhost:3000/oauth2/redirect";

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request, 
            HttpServletResponse response, 
            AuthenticationException exception) throws IOException, ServletException {
        
        log.error("OAuth2 인증 실패: {}", exception.getMessage());
        
        String targetUrl = UriComponentsBuilder.fromUriString(REDIRECT_URI)
                .queryParam("error", "oauth2_login_failure")
                .queryParam("message", exception.getMessage())
                .build().toUriString();
        
        response.sendRedirect(targetUrl);
    }
}
