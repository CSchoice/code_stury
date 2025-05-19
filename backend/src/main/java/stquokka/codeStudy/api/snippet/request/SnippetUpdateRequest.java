package stquokka.codeStudy.api.snippet.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 코드 스니펫 업데이트 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnippetUpdateRequest {
    
    private String title;
    private String code;
    private String language;
    private String description;
    private Boolean isPublic;
    private List<String> tags;
}
