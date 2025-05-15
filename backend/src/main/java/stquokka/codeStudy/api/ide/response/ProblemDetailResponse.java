package stquokka.codeStudy.api.ide.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemDetailResponse {
    private Long id;
    private String title;
    private String description;
    private String inputDescription;
    private String outputDescription;
    private String constraints;
    private String difficulty;
    private List<String> categories;
    private Integer timeLimitSeconds;
    private Integer memoryLimitMb;
    private String sampleCode;
    private List<TestCaseResponse> sampleTestCases;
}
