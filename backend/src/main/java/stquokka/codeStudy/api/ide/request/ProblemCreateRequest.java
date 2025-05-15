package stquokka.codeStudy.api.ide.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemCreateRequest {
    
    @NotBlank(message = "제목은 필수입니다")
    private String title;
    
    @NotBlank(message = "설명은 필수입니다")
    private String description;
    
    private String inputDescription;
    
    private String outputDescription;
    
    private String constraints;
    
    @NotBlank(message = "난이도는 필수입니다")
    private String difficulty;
    
    private List<String> categories;
    
    @NotNull(message = "시간 제한은 필수입니다")
    @Positive(message = "시간 제한은 양수여야 합니다")
    private Integer timeLimitSeconds;
    
    @NotNull(message = "메모리 제한은 필수입니다")
    @Positive(message = "메모리 제한은 양수여야 합니다")
    private Integer memoryLimitMb;
    
    private String sampleCode;
}
