package stquokka.codeStudy.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import stquokka.codeStudy.domain.user.common.MemberRole;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    private String name;
    
    @Column(nullable = false, unique = true)
    private String nickname;
    
    @Column(nullable = false)
    private Integer profileImage;
    
    @Column(nullable = false)
    private String oauthProvider;
    
    @Column(nullable = false)
    private String oauthProviderId;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberRole role;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;
    
    // Role 대신 MemberRole 사용
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * 회원가입 정보 설정
     */
    public void signupMember(String userId, String password) {
        this.email = userId;
        this.password = password;
        this.role = MemberRole.USER;
    }
    
    /**
     * 닉네임 업데이트
     */
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
    
    /**
     * 프로필 이미지 설정
     */
    public void setProfileImage(Integer profileImage) {
        this.profileImage = profileImage;
    }
    
    /**
     * 패스워드 업데이트
     */
    public void updatePassword(String password) {
        this.password = password;
    }
    
    /**
     * 회원 탈퇴 처리
     */
    public void delete() {
        this.isDeleted = true;
    }
    
    /**
     * 탈퇴 여부 확인
     */
    public boolean isDeleted() {
        return this.isDeleted;
    }
    
    /**
     * 사용자 ID 가져오기
     */
    public String getUserId() {
        return this.email;
    }
}
