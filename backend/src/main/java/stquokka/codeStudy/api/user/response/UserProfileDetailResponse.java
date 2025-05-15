package stquokka.codeStudy.api.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import stquokka.codeStudy.domain.user.entity.User;

@Schema(description = "유저 프로필 응답 DTO")
public record UserProfileDetailResponse(
        @Schema(description = "사용자 ID", example = "1")
        String id,
        
        @Schema(description = "닉네임", example = "쿠잉비")
        String nickname,
        
        @Schema(description = "이름", example = "홍길동")
        String name,
        
        @Schema(description = "프로필 이미지 번호", example = "1")
        String profileImage
) {
    public static UserProfileDetailResponse from(User user) {
        return new UserProfileDetailResponse(
                user.getId().toString(),
                user.getNickname(),
                user.getName(),
                user.getProfileImage().toString()
        );
    }
}
