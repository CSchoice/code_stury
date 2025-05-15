package stquokka.codeStudy.domain.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import stquokka.codeStudy.common.properties.JwtProperties;
import stquokka.codeStudy.common.util.JwtProcessor;
import stquokka.codeStudy.domain.auth.model.LoginToken;
import stquokka.codeStudy.domain.auth.service.AuthService;
import stquokka.codeStudy.domain.user.entity.User;
import stquokka.codeStudy.domain.user.service.UserService;

import java.io.IOException;
import java.util.Map;

/**
 * OAuth2 인증 성공 시 처리를 담당하는 핸들러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProcessor jwtProcessor;
    private final JwtProperties jwtProperties;
    private final UserService userService;
    private final AuthService authService;
    
    private static final String REDIRECT_URI = "http://localhost:3000/oauth2/redirect";

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, 
            HttpServletResponse response, 
            Authentication authentication) throws IOException, ServletException {
        
        log.info("OAuth2 인증 성공: {}", authentication.getName());
        
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            processOAuth2User(response, oAuth2User, (OAuth2AuthenticationToken) authentication);
        } else {
            log.warn("지원되지 않는 인증 타입: {}", authentication.getClass());
            response.sendRedirect(REDIRECT_URI + "?error=authentication_type_not_supported");
        }
    }
    
    private void processOAuth2User(
            HttpServletResponse response, 
            OAuth2User oAuth2User, 
            OAuth2AuthenticationToken authToken) throws IOException {
        
        try {
            // OAuth2 제공자와 사용자 ID 추출
            String registrationId = authToken.getAuthorizedClientRegistrationId();
            Map<String, Object> attributes = oAuth2User.getAttributes();
            String providerId = extractProviderId(registrationId, attributes);
            String email = extractEmail(registrationId, attributes);
            String name = extractName(registrationId, attributes);
            
            log.info("OAuth2 사용자 정보: provider={}, providerId={}, email={}", 
                    registrationId, providerId, email);
            
            // 사용자 찾기 또는 생성
            User user = userService.findOrCreateOAuth2User(email, name, registrationId, providerId);
            
            // JWT 토큰 생성
            String accessToken = jwtProcessor.generateAccessToken(user);
            String refreshToken = jwtProcessor.generateRefreshToken(user);
            LoginToken tokens = new LoginToken(accessToken, refreshToken);
            
            // 리프레시 토큰 저장
            jwtProcessor.saveRefreshToken(tokens, user);
            
            // 리디렉션 URL 생성 (토큰을 쿼리 파라미터로 포함)
            String targetUrl = UriComponentsBuilder.fromUriString(REDIRECT_URI)
                    .queryParam("token", accessToken)
                    .queryParam("refresh_token", refreshToken)
                    .build().toUriString();
            
            // 클라이언트로 리디렉션
            response.sendRedirect(targetUrl);
            
        } catch (Exception e) {
            log.error("OAuth2 인증 처리 중 오류 발생", e);
            response.sendRedirect(REDIRECT_URI + "?error=oauth2_processing_error");
        }
    }
    
    /**
     * OAuth2 제공자에 따라 providerId 추출
     */
    private String extractProviderId(String registrationId, Map<String, Object> attributes) {
        switch (registrationId) {
            case "google":
                return (String) attributes.get("sub");
            default:
                throw new IllegalArgumentException("지원되지 않는 OAuth2 제공자: " + registrationId);
        }
    }
    
    /**
     * OAuth2 제공자에 따라 이메일 추출
     */
    private String extractEmail(String registrationId, Map<String, Object> attributes) {
        switch (registrationId) {
            case "google":
                return (String) attributes.get("email");
            default:
                throw new IllegalArgumentException("지원되지 않는 OAuth2 제공자: " + registrationId);
        }
    }
    
    /**
     * OAuth2 제공자에 따라 이름 추출
     */
    private String extractName(String registrationId, Map<String, Object> attributes) {
        switch (registrationId) {
            case "google":
                return (String) attributes.get("name");
            default:
                throw new IllegalArgumentException("지원되지 않는 OAuth2 제공자: " + registrationId);
        }
    }
}
