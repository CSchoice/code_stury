package stquokka.codeStudy.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stquokka.codeStudy.api.user.response.*;
import stquokka.codeStudy.common.exception.User.*;
import stquokka.codeStudy.common.exception.security.InvalidPasswordException;
import stquokka.codeStudy.common.exception.security.NotAuthenticatedException;
import stquokka.codeStudy.common.exception.token.TokenSaveFailedException;
import stquokka.codeStudy.common.util.JwtProcessor;
import stquokka.codeStudy.common.util.SecurityUtil;
import stquokka.codeStudy.domain.user.command.MemberSignupCommand;
import stquokka.codeStudy.domain.user.command.PasswordUpdateCommand;
import stquokka.codeStudy.domain.user.command.UpdateProfileCommand;
import stquokka.codeStudy.domain.user.common.MemberRole;
import stquokka.codeStudy.domain.user.entity.User;
import stquokka.codeStudy.domain.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtProcessor jwtProcessor;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplateLang;
    
    /**
     * OAuth2 인증을 통해 사용자를 찾거나 새로 생성
     */
    @Override
    @Transactional
    public User findOrCreateOAuth2User(String email, String name, String provider, String providerId) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> createOAuth2User(email, name, provider, providerId));
    }
    
    /**
     * OAuth2 인증 사용자 생성
     */
    private User createOAuth2User(String email, String name, String provider, String providerId) {
        User user = User.builder()
                .email(email)
                .name(name)
                .nickname(generateUniqueNickname(name))
                .oauthProvider(provider)
                .oauthProviderId(providerId)
                .role(MemberRole.USER)
                .profileImage(1) // 기본 프로필 이미지
                .build();
        
        return userRepository.save(user);
    }
    
    /**
     * 고유한 닉네임 생성
     */
    private String generateUniqueNickname(String baseName) {
        String nickname = baseName;
        int suffix = 1;
        
        while (userRepository.existsByNickname(nickname)) {
            nickname = baseName + "_" + suffix++;
        }
        
        return nickname;
    }

    @Override
    @Transactional
    public UserSignupResponse signupMember(MemberSignupCommand cmd) {
        if (userRepository.existsByNickname(cmd.nickname())) {
            throw new UserIdDuplicateException();
        }

        User user = User.builder()
                .nickname(cmd.nickname())
                .build();

        String encodedPwd = passwordEncoder.encode(cmd.password());
        user.signupMember(cmd.userId(), encodedPwd);

        userRepository.save(user);

        try {
            String accessToken = jwtProcessor.generateAccessToken(user);
            String refreshToken = jwtProcessor.generateRefreshToken(user);
            jwtProcessor.saveRefreshToken(refreshToken, user.getId());
            return new UserSignupResponse(accessToken, refreshToken);
        } catch (Exception e) {
            throw new TokenSaveFailedException();
        }
    }

    @Override
    public UserDetailResponse getMemberDetail() {
        User u = getCurrentUser();
        return UserDetailResponse.from(u);
    }

    @Override
    public UserProfileDetailResponse getMemberProfileDetail() {
        User u = getCurrentUser();
        return UserProfileDetailResponse.from(u);
    }

    @Override
    public UserIdResponse getMemberId() {
        Long userId = SecurityUtil.getLoginMemberId()
                .orElseThrow(NotAuthenticatedException::new);
        return new UserIdResponse(userId);
    }

    @Override
    @Transactional
    public UserDetailResponse updateMemberProfile(UpdateProfileCommand cmd) {
        User u = getCurrentUser();
        u.updateNickname(cmd.nickname());
        userRepository.save(u);
        return UserDetailResponse.from(u);
    }

    @Override
    @Transactional
    public UserProfileDetailResponse updateProfileImage(Integer profileImageNumber) {
        User u = getCurrentUser();
        u.setProfileImage(profileImageNumber);
        userRepository.save(u);
        return UserProfileDetailResponse.from(u);
    }

    @Override
    @Transactional
    public void updateMemberPassword(PasswordUpdateCommand cmd) {
        User u = getCurrentUser();
        if (!passwordEncoder.matches(cmd.currentPassword(), u.getPassword())) {
            throw new InvalidPasswordException();
        }

        String enc = passwordEncoder.encode(cmd.newPassword());
        u.updatePassword(enc);
        userRepository.save(u);
    }

    @Override
    @Transactional
    public String deleteMember() {
        User u = getCurrentUser();
        u.delete();
        userRepository.save(u);
        return "회원 탈퇴가 성공적으로 처리되었습니다.";
    }

    @Override
    public boolean checkNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    private User getCurrentUser() {
        return userRepository.findById(SecurityUtil.getLoginMemberId()
                        .orElseThrow(NotAuthenticatedException::new))
                .orElseThrow(UserNotFoundException::new);
    }
}