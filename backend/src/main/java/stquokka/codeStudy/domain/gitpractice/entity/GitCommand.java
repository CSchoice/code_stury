package stquokka.codeStudy.domain.gitpractice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "git_commands")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitCommand {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private GitSession session;
    
    @Column(nullable = false)
    private String command;
    
    @Column(columnDefinition = "LONGTEXT")
    private String stdout;
    
    @Column(columnDefinition = "LONGTEXT")
    private String stderr;
    
    @Column(name = "executed_at")
    private LocalDateTime executedAt;
    
    @PrePersist
    protected void onCreate() {
        executedAt = LocalDateTime.now();
    }
}
