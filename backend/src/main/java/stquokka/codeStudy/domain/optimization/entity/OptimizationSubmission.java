package stquokka.codeStudy.domain.optimization.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import stquokka.codeStudy.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "optimization_submissions")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptimizationSubmission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "example_id", nullable = false)
    private OptimizationExample example;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "optimized_code", columnDefinition = "LONGTEXT")
    private String optimizedCode;
    
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;
    
    @OneToOne(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private OptimizationResult result;
    
    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
    }
}
