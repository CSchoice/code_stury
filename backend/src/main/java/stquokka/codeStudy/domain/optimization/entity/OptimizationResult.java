package stquokka.codeStudy.domain.optimization.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "optimization_results")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptimizationResult {
    
    @Id
    private Long submissionId;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "submission_id")
    private OptimizationSubmission submission;
    
    @Column(name = "before_time_ms")
    private Integer beforeTimeMs;
    
    @Column(name = "after_time_ms")
    private Integer afterTimeMs;
    
    @Column(name = "before_memory_mb")
    private Float beforeMemoryMb;
    
    @Column(name = "after_memory_mb")
    private Float afterMemoryMb;
    
    @Column(name = "measured_at")
    private LocalDateTime measuredAt;
    
    @PrePersist
    protected void onCreate() {
        measuredAt = LocalDateTime.now();
    }
}
