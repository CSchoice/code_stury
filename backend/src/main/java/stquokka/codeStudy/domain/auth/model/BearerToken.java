package stquokka.codeStudy.domain.auth.model;

import static stquokka.codeStudy.common.constant.JwtConstants.BEARER;

public record BearerToken(
        String accessToken
) {
    public static BearerToken of(String accessToken) {
        return new BearerToken(BEARER + accessToken);
    }
}
