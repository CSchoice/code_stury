package stquokka.codeStudy.domain.ide.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ide_files")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdeFile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private IdeSession session;
    
    @Column(nullable = false)
    private String filename;
    
    @Column(columnDefinition = "LONGTEXT")
    private String content;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL)
    @Builder.Default
    private java.util.List<CodeExecutionLog> executionLogs = new java.util.ArrayList<>();
    
    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
