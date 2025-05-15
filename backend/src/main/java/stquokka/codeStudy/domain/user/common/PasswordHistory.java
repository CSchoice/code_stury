package stquokka.codeStudy.domain.user.common;

import jakarta.persistence.*;
import lombok.*;
import stquokka.codeStudy.domain.common.BaseEntity;
import stquokka.codeStudy.domain.user.entity.User;

@Getter
@Builder
@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PasswordHistory extends BaseEntity {

    // id 필드는 BaseEntity에서 상속받음

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String password;

    // createdAt, updatedAt, deletedAt 필드는 BaseEntity에서 상속받음

    public void setPasswordHistory(User user, String password) {
        this.user = user;
        this.password = password;
    }
}
