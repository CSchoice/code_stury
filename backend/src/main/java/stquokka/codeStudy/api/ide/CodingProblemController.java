package stquokka.codeStudy.api.ide;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import stquokka.codeStudy.api.CommonResponse;
import stquokka.codeStudy.api.ide.request.ProblemCreateRequest;
import stquokka.codeStudy.api.ide.request.ProblemSubmitRequest;
import stquokka.codeStudy.api.ide.request.TestCaseCreateRequest;
import stquokka.codeStudy.api.ide.response.ProblemDetailResponse;
import stquokka.codeStudy.api.ide.response.ProblemResponse;
import stquokka.codeStudy.api.ide.response.SubmissionResponse;
import stquokka.codeStudy.api.ide.response.TestCaseResponse;
import stquokka.codeStudy.domain.ide.entity.CodingProblem;
import stquokka.codeStudy.domain.ide.entity.TestCase;
import stquokka.codeStudy.domain.ide.service.IdeService;
import stquokka.codeStudy.domain.user.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coding-problems")
@Tag(name = "Coding Problems", description = "코딩 문제 관리")
public class CodingProblemController {
    
    private final IdeService ideService;
    
    @Operation(summary = "문제 목록 조회", description = "코딩 문제 목록을 페이지네이션으로 조회합니다")
    @GetMapping
    public ResponseEntity<CommonResponse<Page<ProblemResponse>>> getProblems(
            @RequestParam(required = false) CodingProblem.Difficulty difficulty,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        
        Page<CodingProblem> problems;
        if (keyword != null && !keyword.isBlank()) {
            problems = ideService.searchProblems(keyword, pageable);
        } else {
            problems = ideService.getProblems(difficulty, category, pageable);
        }
        
        Page<ProblemResponse> response = problems.map(p -> ProblemResponse.builder()
                .id(p.getId())
                .title(p.getTitle())
                .difficulty(p.getDifficulty().name())
                .categories(p.getCategories())
                .build());
        
        return ResponseEntity.ok(CommonResponse.ok(response));
    }
    
    @Operation(summary = "문제 상세 조회", description = "코딩 문제의 상세 정보를 조회합니다")
    @GetMapping("/{problemId}")
    public ResponseEntity<CommonResponse<ProblemDetailResponse>> getProblem(@PathVariable Long problemId) {
        CodingProblem problem = ideService.getProblem(problemId);
        
        // 샘플 테스트 케이스 조회
        List<TestCase> sampleTestCases = ideService.getSampleTestCases(problemId);
        List<TestCaseResponse> testCaseResponses = sampleTestCases.stream()
                .map(tc -> TestCaseResponse.builder()
                        .id(tc.getId())
                        .input(tc.getInput())
                        .expectedOutput(tc.getExpectedOutput())
                        .explanation(tc.getExplanation())
                        .build())
                .collect(Collectors.toList());
        
        ProblemDetailResponse response = ProblemDetailResponse.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .description(problem.getDescription())
                .inputDescription(problem.getInputDescription())
                .outputDescription(problem.getOutputDescription())
                .constraints(problem.getConstraints())
                .difficulty(problem.getDifficulty().name())
                .categories(problem.getCategories())
                .timeLimitSeconds(problem.getTimeLimitSeconds())
                .memoryLimitMb(problem.getMemoryLimitMb())
                .sampleCode(problem.getSampleCode())
                .sampleTestCases(testCaseResponses)
                .build();
        
        return ResponseEntity.ok(CommonResponse.ok(response));
    }
    
    @Operation(summary = "문제 생성", description = "새로운 코딩 문제를 생성합니다")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResponse<Long>> createProblem(
            @Validated @RequestBody ProblemCreateRequest request) {
        
        log.info("코딩 문제 생성 요청");
        
        CodingProblem problem = ideService.createProblem(
                request.getTitle(),
                request.getDescription(),
                request.getInputDescription(),
                request.getOutputDescription(),
                request.getConstraints(),
                CodingProblem.Difficulty.valueOf(request.getDifficulty()),
                request.getCategories(),
                request.getTimeLimitSeconds(),
                request.getMemoryLimitMb(),
                request.getSampleCode()
        );
        
        return ResponseEntity.ok(CommonResponse.ok(problem.getId()));
    }
    
    @Operation(summary = "테스트 케이스 추가", description = "코딩 문제에 테스트 케이스를 추가합니다")
    @PostMapping("/{problemId}/test-cases")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResponse<Long>> createTestCase(
            @PathVariable Long problemId,
            @Validated @RequestBody TestCaseCreateRequest request) {
        
        log.info("문제 [{}]에 테스트 케이스 추가 요청", problemId);
        
        TestCase testCase = ideService.createTestCase(
                problemId,
                request.getInput(),
                request.getExpectedOutput(),
                request.isSample(),
                request.isHidden(),
                request.getExplanation(),
                request.getTestNumber()
        );
        
        return ResponseEntity.ok(CommonResponse.ok(testCase.getId()));
    }
    
    @Operation(summary = "문제 제출", description = "코딩 문제 해결을 위한 코드를 제출합니다")
    @PostMapping("/{problemId}/submit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommonResponse<SubmissionResponse>> submitProblem(
            @PathVariable Long problemId,
            @Validated @RequestBody ProblemSubmitRequest request,
            @AuthenticationPrincipal User user) {
        
        log.info("사용자 [{}]가 문제 [{}] 코드 제출", user.getEmail(), problemId);
        
        Long submissionId = ideService.submitProblem(
                user,
                problemId,
                request.getCode(),
                request.getLanguage()
        );
        
        SubmissionResponse response = SubmissionResponse.builder()
                .submissionId(submissionId)
                .build();
        
        return ResponseEntity.ok(CommonResponse.ok(response));
    }
    
    @Operation(summary = "제출 결과 조회", description = "코드 제출 결과를 조회합니다")
    @GetMapping("/submissions/{submissionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommonResponse<SubmissionResponse>> getSubmissionResult(
            @PathVariable Long submissionId,
            @AuthenticationPrincipal User user) {
        
        SubmissionResponse response = ideService.getSubmissionResult(submissionId, user);
        
        return ResponseEntity.ok(CommonResponse.ok(response));
    }
    
    @Operation(summary = "내 제출 기록 조회", description = "사용자의 문제 제출 기록을 조회합니다")
    @GetMapping("/submissions/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommonResponse<Page<SubmissionResponse>>> getMySubmissions(
            @RequestParam(required = false) Long problemId,
            Pageable pageable,
            @AuthenticationPrincipal User user) {
        
        Page<SubmissionResponse> submissions = ideService.getUserSubmissions(
                user, 
                problemId, 
                pageable
        );
        
        return ResponseEntity.ok(CommonResponse.ok(submissions));
    }
}
