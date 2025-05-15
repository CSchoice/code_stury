package stquokka.codeStudy.api.ide.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeExecutionRequest {
    private String code;
    private String language;
    private String input;
}
