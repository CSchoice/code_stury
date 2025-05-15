package stquokka.codeStudy.domain.ide.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "code_execution_logs")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeExecutionLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private IdeSession session;
    
    @ManyToOne
    @JoinColumn(name = "file_id")
    private IdeFile file;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "execution_type", nullable = false)
    private ExecutionType executionType;
    
    @Column(columnDefinition = "LONGTEXT")
    private String stdout;
    
    @Column(columnDefinition = "LONGTEXT")
    private String stderr;
    
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;
    
    @Column(name = "ended_at")
    private LocalDateTime endedAt;
    
    @Column(name = "memory_used_mb")
    private Float memoryUsedMb;
    
    public enum ExecutionType {
        RUN, TEST
    }
    
    @PrePersist
    protected void onCreate() {
        startedAt = LocalDateTime.now();
    }
    
    public void end(Float memoryUsedMb) {
        this.endedAt = LocalDateTime.now();
        this.memoryUsedMb = memoryUsedMb;
    }
}
