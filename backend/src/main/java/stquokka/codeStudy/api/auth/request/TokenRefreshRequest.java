package stquokka.codeStudy.api.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "토큰 갱신 요청 DTO")
public record TokenRefreshRequest(
        @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0")
        String refreshToken
) {
}
