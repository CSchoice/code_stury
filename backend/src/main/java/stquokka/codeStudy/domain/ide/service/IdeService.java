package stquokka.codeStudy.domain.ide.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import stquokka.codeStudy.api.ide.response.SubmissionResponse;
import stquokka.codeStudy.domain.ide.command.CodeExecutionResult;
import stquokka.codeStudy.domain.ide.entity.CodingProblem;
import stquokka.codeStudy.domain.ide.entity.TestCase;
import stquokka.codeStudy.domain.user.entity.User;

import java.util.List;

/**
 * IDE 관련 기능을 제공하는 서비스 인터페이스
 * 코딩 문제, 테스트 케이스, 코드 실행, 문제 제출 등의 기능을 포함
 */
public interface IdeService {
    
    // 코딩 문제 관련 기능
    Page<CodingProblem> getProblems(Integer difficulty, String category, Pageable pageable);
    Page<CodingProblem> searchProblems(String keyword, Pageable pageable);
    CodingProblem getProblem(Long problemId);
    List<TestCase> getSampleTestCases(Long problemId);
    List<TestCase> getPublicTestCases(Long problemId);
    List<TestCase> getAllTestCases(Long problemId);
    CodingProblem createProblem(String title, String description, String inputDescription, String outputDescription, 
                                String constraints, Integer difficulty, List<String> categories, 
                                Integer timeLimitSeconds, Integer memoryLimitMb, String sampleCode);
    TestCase createTestCase(Long problemId, String input, String expectedOutput, boolean isSample, 
                            boolean isHidden, String explanation, Integer testNumber);
    void updateProblem(Long problemId, CodingProblem updatedProblem);
    void deactivateProblem(Long problemId);
    
    // 코드 실행 관련 기능
    CodeExecutionResult executeCode(String code, String language, String input);
    CodeExecutionResult executeCodeWithTestCase(String code, String language, Long testCaseId);
    
    // 문제 제출 관련 기능
    Long submitProblem(User user, Long problemId, String code, String language);
    void evaluateSubmission(Long submissionId);
    SubmissionResponse getSubmissionResult(Long submissionId, User user);
    Page<SubmissionResponse> getUserSubmissions(User user, Long problemId, Pageable pageable);
}
