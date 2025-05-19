package stquokka.codeStudy.api.snippet.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 코드 스니펫 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnippetResponse {
    
    private Long id;
    private String ownerUsername;
    private Long ownerId;
    private String title;
    private String code;
    private String language;
    private String description;
    private boolean isPublic;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer viewCount;
    private Integer likeCount;
    private String shareToken;
}
