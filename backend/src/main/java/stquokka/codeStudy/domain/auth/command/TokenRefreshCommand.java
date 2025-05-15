package stquokka.codeStudy.domain.auth.command;

public record TokenRefreshCommand(
        String refreshToken
) {
}
