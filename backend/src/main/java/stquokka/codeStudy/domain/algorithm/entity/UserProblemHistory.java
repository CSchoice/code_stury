package stquokka.codeStudy.domain.algorithm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import stquokka.codeStudy.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_problem_history")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProblemHistory {
    
    @EmbeddedId
    private UserProblemHistoryId id;
    
    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne
    @MapsId("problemId")
    @JoinColumn(name = "problem_id")
    private Problem problem;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    
    @Column(nullable = false)
    private int attempts;
    
    @Column(name = "last_attempted_at")
    private LocalDateTime lastAttemptedAt;
    
    public enum Status {
        ATTEMPTED, SOLVED, FAILED
    }
    
    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastAttemptedAt = LocalDateTime.now();
    }
}
