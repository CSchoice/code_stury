package stquokka.codeStudy.domain.snippet.entity;

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
@Table(name = "code_snippets")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeSnippet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String code;
    
    @Column(nullable = false)
    private String language;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private boolean isPublic = false;
    
    @ElementCollection
    @CollectionTable(name = "code_snippet_tags", joinColumns = @JoinColumn(name = "snippet_id"))
    @Column(name = "tag")
    @Builder.Default
    private List<String> tags = new ArrayList<>();
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private Integer viewCount = 0;
    
    @Column(name = "like_count", nullable = false)
    @Builder.Default
    private Integer likeCount = 0;
    
    @Column(name = "share_token")
    private String shareToken;
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public void incrementViewCount() {
        this.viewCount++;
    }
    
    public void incrementLikeCount() {
        this.likeCount++;
    }
    
    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }
    
    public void setShareToken(String shareToken) {
        this.shareToken = shareToken;
    }
}
