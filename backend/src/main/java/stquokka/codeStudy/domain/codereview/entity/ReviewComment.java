package stquokka.codeStudy.domain.codereview.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_comments")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewComment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "request_id", nullable = false)
    private ReviewRequest request;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;
    
    @Column(nullable = false)
    private String message;
    
    @Column(name = "line_number")
    private Integer lineNumber;
    
    @Column(name = "column_number")
    private Integer columnNumber;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public enum Severity {
        HIGH, MEDIUM, LOW
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
