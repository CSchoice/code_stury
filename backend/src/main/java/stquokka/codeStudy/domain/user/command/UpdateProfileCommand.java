package stquokka.codeStudy.domain.user.command;

import org.springframework.web.multipart.MultipartFile;

public record UpdateProfileCommand(
        String nickname
//        MultipartFile profileImagePath,
//        boolean deleteImage
) {}
