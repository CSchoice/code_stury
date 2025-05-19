package stquokka.codeStudy.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.config.Customizer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import stquokka.codeStudy.common.exception.handler.CustomAccessDeniedHandler;
import stquokka.codeStudy.common.exception.handler.CustomAuthenticationEntryPoint;
import stquokka.codeStudy.common.properties.CorsProperties;
import stquokka.codeStudy.config.filter.CacheControlFilter;
import stquokka.codeStudy.config.filter.JwtAuthenticationFilter;

// 삭제된 패키지의 상수 참조 제거

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    // 삭제된 패키지의 상수를 대체하기 위한 내부 상수 정의
    private static final String[] AUTHENTICATED_ONLY = {
        "/api/user/**",
        "/api/admin/**"
    };

    private final CorsProperties corsProperties;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfig()))
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())  // Prometheus를 위한 Basic 인증 활성화
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(AUTHENTICATED_ONLY).authenticated() // 내부 상수 사용
                        .requestMatchers("/actuator/prometheus").hasRole("PROMETHEUS")  // Prometheus 역할 접근 권한 설정
                        .requestMatchers("/actuator/**").hasRole("ADMIN")
//                        .requestMatchers(PathRequest.toH2Console()).permitAll()
                        .anyRequest().permitAll())
                .addFilterAt(jwtAuthenticationFilter, BasicAuthenticationFilter.class) // addFilterAt으로 위치 지정
                .addFilterBefore(new CacheControlFilter(), JwtAuthenticationFilter.class) // CacheControlFilter 추가
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.accessDeniedHandler(customAccessDeniedHandler)
                                .authenticationEntryPoint(customAuthenticationEntryPoint))
                .build();
    }


    private CorsConfigurationSource corsConfig() {
        log.debug(">>> [SecurityConfig::configurationSource] CorsConfigurationSource CORS 설정이 filterchain에 등록");

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(corsProperties.allowedOrigins());
        configuration.setAllowedMethods(corsProperties.allowedMethods());
        configuration.setAllowedHeaders(corsProperties.allowedHeaders());

        configuration.addAllowedHeader("cache-control");
        
        configuration.setAllowCredentials(corsProperties.allowedCredentials());
        configuration.setExposedHeaders(corsProperties.exposedHeaders());
        configuration.setMaxAge(corsProperties.oneHour());

        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        corsConfigurationSource.registerCorsConfiguration("/**", configuration);

        return corsConfigurationSource;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
