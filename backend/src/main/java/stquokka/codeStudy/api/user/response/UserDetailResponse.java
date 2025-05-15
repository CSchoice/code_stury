package stquokka.codeStudy.api.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import stquokka.codeStudy.domain.user.entity.User;

@Schema(description = "유저 정보 응답 DTO")
public record UserDetailResponse(
        @Schema(description = "사용자 ID", example = "1")
        String id,
        
        @Schema(description = "이메일", example = "user@example.com")
        String email,
        
        @Schema(description = "이름", example = "홍길동")
        String name,
        
        @Schema(description = "닉네임", example = "쿠잉비")
        String nickname,
        
        @Schema(description = "프로필 이미지 번호", example = "1")
        String profileImage
) {
    public static UserDetailResponse from(User user) {
        return new UserDetailResponse(
                user.getId().toString(),
                user.getEmail(),
                user.getName(),
                user.getNickname(),
                user.getProfileImage().toString()
        );
    }
}
