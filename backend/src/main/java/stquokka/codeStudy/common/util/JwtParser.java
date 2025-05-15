package stquokka.codeStudy.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.IncorrectClaimException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import stquokka.codeStudy.common.exception.security.IssuerTokenIncorrectException;
import stquokka.codeStudy.common.exception.security.TokenExpiredException;
import stquokka.codeStudy.common.exception.security.InvalidSignatureTokenException;
import stquokka.codeStudy.common.exception.security.InvalidTokenException;
import stquokka.codeStudy.domain.auth.model.UserInfo;

import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtParser {

    private final ObjectMapper objectMapper;

    public String getKid(String idToken) {
        try {
            String[] tokenSplit = idToken.split("\\.");
            String header = tokenSplit[0];
            String decodedHeader = new String(Base64.getUrlDecoder().decode(header), StandardCharsets.UTF_8);
            return objectMapper.readValue(decodedHeader, Map.class).get("kid").toString();
        } catch (Exception e) {
            throw new InvalidTokenException();
        }
    }

    public UserInfo getUserInfo(String idToken, RSAPublicKey key, String iss, String aud) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .requireIssuer(iss)
                    .requireAudience(aud)
                    .build()
                    .parseClaimsJws(idToken)
                    .getBody();
            return new UserInfo(claims.get("nickname", String.class), claims.get("email", String.class), claims.get("profile_image", String.class));
        } catch (SignatureException e) {
            throw new InvalidSignatureTokenException();
        } catch (IncorrectClaimException e) {
            throw new IssuerTokenIncorrectException();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException();
        } catch (Exception e) {
            throw new InvalidTokenException();
        }
    }

    // Kakao 로그인 처리 메소드는 임시로 제거되었습니다.
    // KakaoLoginCommand 클래스가 생성된 후 다시 구현 필요

}
