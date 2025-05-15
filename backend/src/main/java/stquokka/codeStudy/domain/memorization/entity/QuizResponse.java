package stquokka.codeStudy.domain.memorization.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import stquokka.codeStudy.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_responses")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponse {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "selected_answer")
    private String selectedAnswer;
    
    @Column(name = "is_correct")
    private Boolean isCorrect;
    
    @Column(name = "answered_at")
    private LocalDateTime answeredAt;
    
    @PrePersist
    protected void onCreate() {
        answeredAt = LocalDateTime.now();
    }
}
