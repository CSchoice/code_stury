package stquokka.codeStudy.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import stquokka.codeStudy.domain.user.entity.User;
import stquokka.codeStudy.domain.user.repository.UserRepository;

@RequiredArgsConstructor
@Component
public class UserUtil {
    private final UserRepository userRepository;
    public User getCurrentUser() {
        Long userId = SecurityUtil.getLoginMemberId()
                .orElseThrow(() -> new RuntimeException("로그인한 사용자가 존재하지 않습니다."));
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));
    }
}
