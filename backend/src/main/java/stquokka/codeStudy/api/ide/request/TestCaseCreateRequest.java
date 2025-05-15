package stquokka.codeStudy.api.ide.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseCreateRequest {
    
    @NotBlank(message = "입력 값은 필수입니다")
    private String input;
    
    @NotBlank(message = "예상 출력 값은 필수입니다")
    private String expectedOutput;
    
    @NotNull(message = "샘플 테스트 여부는 필수입니다")
    private boolean sample;
    
    @NotNull(message = "숨김 테스트 여부는 필수입니다")
    private boolean hidden;
    
    private String explanation;
    
    private Integer testNumber;
}
