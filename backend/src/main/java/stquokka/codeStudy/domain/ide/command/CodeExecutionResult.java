package stquokka.codeStudy.domain.ide.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 코드 실행 결과를 담는 클래스
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeExecutionResult {
    private boolean success;
    private String output;
    private String error;
    private Long executionTime;
    private Float memoryUsed;
}
