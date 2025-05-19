package stquokka.codeStudy.domain.ide.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stquokka.codeStudy.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "problem_submissions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemSubmission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "problem_id", nullable = false)
    private CodingProblem problem;
    
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String submittedCode;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false)
    private ProgrammingLanguage language;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubmissionStatus status;
    
    @Column(name = "score")
    private Integer score;
    
    @Column(name = "execution_time_ms")
    private Integer executionTimeMs;
    
    @Column(name = "memory_used_mb")
    private Float memoryUsedMb;
    
    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @ElementCollection
    @CollectionTable(name = "test_case_results", joinColumns = @JoinColumn(name = "submission_id"))
    @Builder.Default
    private List<TestCaseResult> testCaseResults = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        this.submittedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = SubmissionStatus.PENDING;
        }
    }
    
    public enum ProgrammingLanguage {
        JAVA, PYTHON, JAVASCRIPT, CPP, GO
    }
    
    public enum SubmissionStatus {
        PENDING, RUNNING, ACCEPTED, WRONG_ANSWER, TIME_LIMIT_EXCEEDED, 
        MEMORY_LIMIT_EXCEEDED, RUNTIME_ERROR, COMPILATION_ERROR
    }
    
    @Embeddable
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TestCaseResult {
        @Column(name = "test_case_id")
        private Long testCaseId;
        
        @Column(name = "passed")
        private Boolean passed;
        
        @Column(name = "execution_time_ms")
        private Integer executionTimeMs;
        
        @Column(name = "memory_used_mb")
        private Float memoryUsedMb;
        
        @Column(name = "actual_output", columnDefinition = "LONGTEXT")
        private String actualOutput;
        
        @Column(name = "error_message", columnDefinition = "LONGTEXT")
        private String errorMessage;
    }
}
