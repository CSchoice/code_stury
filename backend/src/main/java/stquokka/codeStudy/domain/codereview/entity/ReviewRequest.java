package stquokka.codeStudy.domain.codereview.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import stquokka.codeStudy.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "review_requests")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String language;
    
    @Column(nullable = false, name = "source_code", columnDefinition = "LONGTEXT")
    private String sourceCode;
    
    @Column(name = "requested_at")
    private LocalDateTime requestedAt;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    
    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewComment> comments = new ArrayList<>();
    
    public enum Status {
        PENDING, COMPLETED, FAILED
    }
    
    @PrePersist
    protected void onCreate() {
        requestedAt = LocalDateTime.now();
    }
}
