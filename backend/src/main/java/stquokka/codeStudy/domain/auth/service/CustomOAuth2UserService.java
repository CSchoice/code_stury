package stquokka.codeStudy.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stquokka.codeStudy.domain.user.entity.User;
import stquokka.codeStudy.domain.user.service.UserService;

import java.util.Collections;
import java.util.Map;

/**
 * OAuth2 사용자 정보를 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();
    private final UserService userService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest);
        
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();
        
        log.info("OAuth2 사용자 로드: provider={}, attributes={}", 
                registrationId, oAuth2User.getAttributes());
        
        // 사용자 속성 처리
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> processedAttributes = processUserAttributes(registrationId, attributes);
        
        // 사용자 정보 추출
        String email = extractEmail(registrationId, attributes);
        String name = extractName(registrationId, attributes);
        String providerId = extractProviderId(registrationId, attributes);
        
        // 사용자 찾기 또는 생성
        User user = userService.findOrCreateOAuth2User(email, name, registrationId, providerId);
        
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                processedAttributes,
                userNameAttributeName
        );
    }
    
    /**
     * OAuth2 제공자별로 속성을 처리
     * 
     * 각 제공자(Google, Facebook 등)에서 전달받은 사용자 정보를 통일된 형식으로 변환합니다.
     */
    private Map<String, Object> processUserAttributes(String registrationId, Map<String, Object> attributes) {
        // 프로바이더별 속성 처리 로직
        // 필요한 경우 속성 변환 또는 추가 정보 설정
        
        // 현재는 원본 속성을 그대로 반환
        return attributes;
    }
    
    /**
     * OAuth2 제공자에 따라 providerId 추출
     */
    private String extractProviderId(String registrationId, Map<String, Object> attributes) {
        if ("google".equals(registrationId)) {
            return (String) attributes.get("sub");
        } else if ("facebook".equals(registrationId)) {
            return (String) attributes.get("id");
        } else if ("kakao".equals(registrationId)) {
            return String.valueOf(attributes.get("id"));
        } else if ("naver".equals(registrationId)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return (String) response.get("id");
        }
        return null;
    }
    
    /**
     * OAuth2 제공자에 따라 이메일 추출
     */
    private String extractEmail(String registrationId, Map<String, Object> attributes) {
        if ("google".equals(registrationId)) {
            return (String) attributes.get("email");
        } else if ("facebook".equals(registrationId)) {
            return (String) attributes.get("email");
        } else if ("kakao".equals(registrationId)) {
            Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
            return (String) account.get("email");
        } else if ("naver".equals(registrationId)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return (String) response.get("email");
        }
        return null;
    }
    
    /**
     * OAuth2 제공자에 따라 이름 추출
     */
    private String extractName(String registrationId, Map<String, Object> attributes) {
        if ("google".equals(registrationId)) {
            return (String) attributes.get("name");
        } else if ("facebook".equals(registrationId)) {
            return (String) attributes.get("name");
        } else if ("kakao".equals(registrationId)) {
            Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
            return (String) properties.get("nickname");
        } else if ("naver".equals(registrationId)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return (String) response.get("name");
        }
        return null;
    }
}
