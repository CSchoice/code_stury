package stquokka.codeStudy.domain.auth.model;

public record LoginToken(
        String accessToken,
        String refreshToken
) {
}
