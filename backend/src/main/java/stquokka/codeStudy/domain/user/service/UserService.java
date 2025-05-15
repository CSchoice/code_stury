package stquokka.codeStudy.domain.user.service;

import stquokka.codeStudy.api.user.response.*;
import stquokka.codeStudy.domain.user.command.MemberSignupCommand;
import stquokka.codeStudy.domain.user.command.PasswordUpdateCommand;
import stquokka.codeStudy.domain.user.command.UpdateProfileCommand;
import stquokka.codeStudy.domain.user.entity.User;

public interface UserService {

    // OAuth2 인증 사용자 처리
    User findOrCreateOAuth2User(String email, String name, String provider, String providerId);

    // 회원가입
    UserSignupResponse signupMember(MemberSignupCommand signupCommand);

    // 회원 정보 조회
    UserDetailResponse getMemberDetail();
    UserProfileDetailResponse getMemberProfileDetail();
    UserIdResponse getMemberId();

    // 회원 정보 수정
    UserDetailResponse updateMemberProfile(UpdateProfileCommand command);
    UserProfileDetailResponse updateProfileImage(Integer profileImageNumber);
    void updateMemberPassword(PasswordUpdateCommand command);

    // 회원 탈퇴
    String deleteMember();

    // 중복 체크
    boolean checkNickname(String nickname);
}
