package stquokka.codeStudy.common.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import stquokka.codeStudy.common.exception.security.TokenExpiredException;
import stquokka.codeStudy.common.exception.security.InvalidSignatureTokenException;
import stquokka.codeStudy.common.exception.security.InvalidTokenException;
import stquokka.codeStudy.common.exception.token.TokenTypeNotMatchedException;
import stquokka.codeStudy.common.properties.JwtProperties;
import stquokka.codeStudy.domain.auth.model.DecodedJwtToken;
import stquokka.codeStudy.domain.auth.model.LoginToken;
import stquokka.codeStudy.domain.user.common.MemberRole;
import stquokka.codeStudy.domain.user.entity.User;
import stquokka.codeStudy.domain.redis.BlacklistTokenRedisRepository;
import stquokka.codeStudy.domain.redis.RefreshTokenRedisRepository;

import java.security.Key;
import java.util.Date;
import java.util.Set;

import stquokka.codeStudy.common.constant.JwtConstants;

@Component
@Slf4j
public class JwtProcessor {
    
    public JwtProcessor(JwtProperties jwtProperties, 
                      BlacklistTokenRedisRepository blacklistTokenRedisRepository, 
                      RefreshTokenRedisRepository refreshTokenRedisRepository) {
        this.jwtProperties = jwtProperties;
        this.blacklistTokenRedisRepository = blacklistTokenRedisRepository;
        this.refreshTokenRedisRepository = refreshTokenRedisRepository;
        initJwtParser();
    }
    
    private void initJwtParser() {
        this.jwtParser = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build();
        log.debug("JWT Parser initialized");
    }

    private final JwtProperties jwtProperties;
private final BlacklistTokenRedisRepository blacklistTokenRedisRepository;
private final RefreshTokenRedisRepository refreshTokenRedisRepository;
private JwtParser jwtParser;

    public Key getSecretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.secretKey().getBytes());
    }

    public Jws<Claims> getClaim(String token) {
        log.debug("token : {}", token);
        if (isTokenExpired(token)) {
            throw new TokenExpiredException();
        }
        try {
            return jwtParser.parseClaimsJws(token);
        } catch (SignatureException e) {
            throw new InvalidSignatureTokenException();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException();
        } catch (Exception e) {
            throw new InvalidTokenException();
        }
    }

    public void saveRefreshToken(String refreshToken, Long userId) {
        refreshTokenRedisRepository.save(refreshToken, userId.toString());
    }

    public void saveRefreshToken(LoginToken tokens, User user) {
        refreshTokenRedisRepository.save(tokens.refreshToken(), user.getId().toString());
    }

    public Long findUserIdByRefreshToken(String refreshToken) {
        String userId = refreshTokenRedisRepository.findById(refreshToken)
                .orElseThrow(InvalidTokenException::new);
        return Long.valueOf(userId);
    }

    public void renewRefreshToken(String oldRefreshToken, String newRefreshToken, User member) {
        refreshTokenRedisRepository.save(newRefreshToken, String.valueOf(member.getId()));
        expireToken(oldRefreshToken);
    }
    
    /**
     * 리프레시 토큰을 즉시 무효화합니다.
     * 토큰을 블랙리스트에 추가하고 저장소에서 제거합니다.
     * @param refreshToken 무효화할 리프레시 토큰
     */
    public void invalidateRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            log.warn("무효화할 리프레시 토큰이 null입니다");
            return;
        }
        
        try {
            // 블랙리스트에 토큰 추가 (만료 시간까지)
            blacklistTokenRedisRepository.save(refreshToken, getRemainingTime(refreshToken));
            // Redis에서 토큰 제거
            refreshTokenRedisRepository.delete(refreshToken);
            log.info("리프레시 토큰 무효화 완료: {}", refreshToken);
        } catch (Exception e) {
            log.error("리프레시 토큰 무효화 실패: {}", e.getMessage());
            throw new InvalidTokenException("토큰 무효화 중 오류 발생");
        }
    }

    public void expireToken(String refreshToken) {
        if (refreshToken == null) {
            log.info("리프레시 토큰이 null이어서 토큰 만료 처리 건너뜀");
            return;
        }
        blacklistTokenRedisRepository.save(refreshToken, getRemainingTime(refreshToken));
        refreshTokenRedisRepository.delete(refreshToken);
        log.info("Token added to blacklist: {}", refreshToken);
    }
    
    /**
     * 사용자 ID로 모든 리프레시 토큰을 만료시킵니다.
     * @param userId 사용자 ID
     * @return 만료된 토큰 수
     */
    public int expireAllUserTokens(Long userId) {
        Set<String> userTokens = refreshTokenRedisRepository.findKeysByValue(userId.toString());
        int count = 0;
        
        for (String token : userTokens) {
            try {
                blacklistTokenRedisRepository.save(token, getRemainingTime(token));
                refreshTokenRedisRepository.delete(token);
                count++;
                log.info("Expired token for user {}: {}", userId, token);
            } catch (Exception e) {
                log.error("Error expiring token for user {}: {}", userId, e.getMessage());
            }
        }
        
        log.info("Expired {} tokens for user {}", count, userId);
        return count;
    }

    public long getRemainingTime(String token) {
        Claims claims = getClaim(token).getBody();
        Date expiration = claims.getExpiration();
        Date now = new Date();
        return Math.max(0, expiration.getTime() - now.getTime());
    }

    public boolean isTokenExpired(String token) {
        Boolean blacklisted = blacklistTokenRedisRepository.hasKey(token);
        if (Boolean.TRUE.equals(blacklisted)) {
            return true;
        }
        try {
            Claims claims = jwtParser.parseClaimsJws(token).getBody();
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            log.warn("Token validation error: {}", e.getMessage());
            return true;
        }
    }

    public String generateAccessToken(User user) {
        log.debug("access token exp : {}", jwtProperties.accessTokenExp());
        return issueToken(user.getId(), user.getRole(), JwtConstants.ACCESS_TOKEN, jwtProperties.accessTokenExp());
    }

    public String generateRefreshToken(User member) {
        return issueToken(member.getId(), member.getRole(), JwtConstants.REFRESH_TOKEN, jwtProperties.refreshTokenExp());
    }

    public DecodedJwtToken decodeToken(String token, String type) {
        Claims claims = getClaim(token).getBody();
        checkType(claims, type);

        return new DecodedJwtToken(
                Long.valueOf(claims.getSubject()),
                String.valueOf(claims.get("role")),
                String.valueOf(claims.get("type"))
        );
    }

    private String issueToken(Long userId, MemberRole role, String type, Long time) {
        Date now = new Date();
        return Jwts.builder()
                .setIssuer("Cooing Inc.")
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + time))
                .claim("type", type)
                .claim("role", role.name())
                .signWith(getSecretKey())
                .compact();
    }

    private void checkType(Claims claims, String type) {
        if (!type.equals(String.valueOf(claims.get("type")))) {
            throw new TokenTypeNotMatchedException();
        }
    }

    

}
