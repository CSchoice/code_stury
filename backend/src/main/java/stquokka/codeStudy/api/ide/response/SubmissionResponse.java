package stquokka.codeStudy.api.ide.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionResponse {
    private Long submissionId;
    private Long problemId;
    private String status;
    private Integer score;
    private String language;
    private Integer executionTimeMs;
    private Float memoryUsedMb;
    private LocalDateTime submittedAt;
    private LocalDateTime completedAt;
    private String code;
    private List<TestResultResponse> testResults;
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestResultResponse {
        private Long testCaseId;
        private Boolean passed;
        private Integer executionTimeMs;
        private Float memoryUsedMb;
        private String actualOutput;
        private String errorMessage;
    }
}
