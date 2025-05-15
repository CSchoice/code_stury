package stquokka.codeStudy.api.ide.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeExecutionResponse {
    private boolean success;
    private String output;
    private String error;
    private Long executionTimeMs;
    private Float memoryUsedMb;
}
