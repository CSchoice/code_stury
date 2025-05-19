package stquokka.codeStudy.api.snippet.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 코드 스니펫 생성 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnippetCreateRequest {
    
    @NotBlank(message = "제목은 필수입니다")
    private String title;
    
    @NotBlank(message = "코드는 필수입니다")
    private String code;
    
    @NotBlank(message = "프로그래밍 언어는 필수입니다")
    private String language;
    
    private String description;
    
    @NotNull(message = "공개 여부는 필수입니다")
    private Boolean isPublic;
    
    private List<String> tags;
}
