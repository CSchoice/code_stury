package stquokka.codeStudy.api.user.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "프로필 이미지 잠금 해제 응답 DTO")
public record ProfileUnlockedResponse(
        @Schema(description = "프로필 이미지 번호", example = "1")
        Integer imageNumber,
        
        @Schema(description = "잠금 해제 여부", example = "true")
        boolean isUnlocked
) {
    public static ProfileUnlockedResponse of(Integer imageNumber, boolean isUnlocked) {
        return new ProfileUnlockedResponse(imageNumber, isUnlocked);
    }
}
