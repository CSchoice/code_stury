package stquokka.codeStudy.domain.ide.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "coding_problems")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodingProblem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String description;
    
    @Column(columnDefinition = "LONGTEXT")
    private String inputDescription;
    
    @Column(columnDefinition = "LONGTEXT")
    private String outputDescription;
    
    @Column(columnDefinition = "LONGTEXT")
    private String constraints;
    
    @Column(name = "difficulty")
    private Integer difficulty; // 1-30 (백준), 1-3 (프로그래머스)
    
    @Column(name = "time_limit_seconds", nullable = false)
    private Integer timeLimitSeconds;
    
    @Column(name = "memory_limit_mb", nullable = false)
    private Integer memoryLimitMb;
    
    @ElementCollection
    @CollectionTable(name = "problem_categories", joinColumns = @JoinColumn(name = "problem_id"))
    @Column(name = "category")
    @Builder.Default
    private List<String> categories = new ArrayList<>();
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(columnDefinition = "LONGTEXT")
    private String sampleCode;
    
    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TestCase> testCases = new ArrayList<>();
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.isActive == null) {
            this.isActive = true;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // 난이도 수치 매핑
    // 백준: solved.ac 기준 1-30
    // 프로그래머스: 1(EASY), 2(MEDIUM), 3(HARD)
}
