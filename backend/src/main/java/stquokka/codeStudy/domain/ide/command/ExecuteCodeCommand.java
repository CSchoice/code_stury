package stquokka.codeStudy.domain.ide.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeCommand {
    private String code;
    private String language;
    private String input;
    private Long sessionId;
    private Long fileId;
}
