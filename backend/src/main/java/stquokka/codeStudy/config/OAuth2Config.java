package stquokka.codeStudy.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import stquokka.codeStudy.domain.auth.handler.OAuth2AuthenticationFailureHandler;
import stquokka.codeStudy.domain.auth.handler.OAuth2AuthenticationSuccessHandler;
import stquokka.codeStudy.domain.auth.service.CustomOAuth2UserService;

/**
 * OAuth2 인증을 위한 설정 클래스
 */
@Configuration
@RequiredArgsConstructor
public class OAuth2Config {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    
    /**
     * OAuth2 로그인 필터 설정
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }
    
    @Bean
    public OAuth2LoginAuthenticationFilter oAuth2LoginAuthenticationFilter(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService,
            AuthenticationManager authenticationManager) {
        
        OAuth2LoginAuthenticationFilter filter = new OAuth2LoginAuthenticationFilter(
                clientRegistrationRepository, authorizedClientService);
                
        // 요청 URL 경로 설정
        filter.setFilterProcessesUrl("/api/v1/oauth2/login");
        
        // 인증 성공/실패 핸들러 설정
        filter.setAuthenticationSuccessHandler(oAuth2AuthenticationSuccessHandler);
        filter.setAuthenticationFailureHandler(oAuth2AuthenticationFailureHandler);
        
        // AuthenticationManager 설정 추가
        filter.setAuthenticationManager(authenticationManager);
        
        return filter;
    }
    
    /**
     * OAuth2 인증 요청 리졸버 설정
     */
    @Bean
    public OAuth2AuthorizationRequestResolver authorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository) {
        
        DefaultOAuth2AuthorizationRequestResolver resolver = 
                new DefaultOAuth2AuthorizationRequestResolver(
                        clientRegistrationRepository, "/api/v1/oauth2/authorization");
        
        return resolver;
    }
}
