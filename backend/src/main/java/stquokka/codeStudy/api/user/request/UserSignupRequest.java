package stquokka.codeStudy.api.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import stquokka.codeStudy.domain.user.command.MemberSignupCommand;

@Schema(description = "유저 회원가입 요청 DTO")
public record UserSignupRequest(
        @Schema(description = "닉네임", example = "쿠잉비", minimum = "2", maximum = "20")
        String nickname,

        @Schema(description = "비밀번호", example = "password123!")
        String password,

        @Schema(description = "유저id", example = "test1")
        String userId
){
        public MemberSignupCommand toCommand() {
                return new MemberSignupCommand(nickname, password, userId);
        }
}
