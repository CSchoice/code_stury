package stquokka.codeStudy.api.ide.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemSubmitRequest {
    
    @NotBlank(message = "코드는 필수입니다")
    private String code;
    
    @NotBlank(message = "프로그래밍 언어는 필수입니다")
    private String language;
}
