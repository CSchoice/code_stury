package stquokka.codeStudy.domain.user.command;

import org.springframework.web.multipart.MultipartFile;

public record MemberSignupCommand(
        String nickname,
        String password,
//        MultipartFile imageUrl,
        String userId
) {
}

