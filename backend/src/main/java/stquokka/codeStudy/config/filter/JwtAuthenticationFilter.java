package stquokka.codeStudy.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import stquokka.codeStudy.domain.auth.model.DecodedJwtToken;
import stquokka.codeStudy.common.util.JwtProcessor;
import stquokka.codeStudy.common.constant.JwtConstants;
import stquokka.codeStudy.common.exception.security.TokenExpiredException;
import stquokka.codeStudy.common.exception.security.InvalidSignatureTokenException;
import stquokka.codeStudy.common.exception.security.InvalidTokenException;
import stquokka.codeStudy.common.exception.token.TokenTypeNotMatchedException;
import stquokka.codeStudy.common.model.ErrorResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProcessor jwtProcessor;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = resolveToken(request);
            if (Objects.nonNull(token)) {
                Authentication authentication = getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (TokenExpiredException e) {
            log.error("TokenExpiredException: {}", e.getMessage());
            setErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token expired: " + e.getMessage());
            return;
        } catch (InvalidSignatureTokenException e) {
            log.error("InvalidSignatureTokenException: {}", e.getMessage());
            setErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid signature: " + e.getMessage());
            return;
        } catch (InvalidTokenException e) {
            log.error("InvalidTokenException: {}", e.getMessage());
            setErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token: " + e.getMessage());
            return;
        } catch (TokenTypeNotMatchedException e) {
            log.error("TokenTypeNotMatchedException: {}", e.getMessage());
            setErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Token type mismatch: " + e.getMessage());
            return;
        } catch (Exception e) {
            log.error("Unhandled exception: {}", e.getMessage());
            setErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error: " + e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private Authentication getAuthentication(String token) {
        DecodedJwtToken decodedJwtToken = jwtProcessor.decodeToken(token, JwtConstants.ACCESS_TOKEN);

        Long memberId = decodedJwtToken.memberId();
        String role = decodedJwtToken.role();
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));

        return new UsernamePasswordAuthenticationToken(memberId, null, authorities);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        } else if (bearerToken != null && !bearerToken.isBlank()) {
            return bearerToken;
        }
        return null;
    }

    private void setErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        ErrorResponse errorResponse = new ErrorResponse(statusCode, message);
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String authHeader = request.getHeader("Authorization");
        
        // Prometheus 요청에 대해 JWT 필터 건너뛰기
        if (path.startsWith("/actuator/prometheus")) {
            return true;
        }
        
        // Basic 인증 요청에 대해 JWT 필터 건너뛰기 (Prometheus는 Basic 인증 사용)
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            return true;
        }
        
        return false;
    }
}
