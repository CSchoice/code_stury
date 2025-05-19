package stquokka.codeStudy.domain.ide.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stquokka.codeStudy.api.ide.response.SubmissionResponse;
import stquokka.codeStudy.domain.ide.entity.CodeExecutionLog;
import stquokka.codeStudy.domain.ide.entity.CodingProblem;
import stquokka.codeStudy.domain.ide.entity.IdeFile;
import stquokka.codeStudy.domain.ide.entity.IdeSession;
import stquokka.codeStudy.domain.ide.entity.ProblemSubmission;
import stquokka.codeStudy.domain.ide.entity.TestCase;
import stquokka.codeStudy.domain.ide.repository.CodeExecutionLogRepository;
import stquokka.codeStudy.domain.ide.repository.CodingProblemRepository;
import stquokka.codeStudy.domain.ide.repository.IdeFileRepository;
import stquokka.codeStudy.domain.ide.repository.IdeSessionRepository;
import stquokka.codeStudy.domain.ide.repository.ProblemSubmissionRepository;
import stquokka.codeStudy.domain.ide.command.CodeExecutionResult;
import stquokka.codeStudy.domain.ide.repository.TestCaseRepository;
import stquokka.codeStudy.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdeServiceImpl implements IdeService {
    
    private final CodingProblemRepository problemRepository;
    private final TestCaseRepository testCaseRepository;
    private final ProblemSubmissionRepository submissionRepository;
    private final IdeSessionRepository sessionRepository;
    private final IdeFileRepository fileRepository;
    private final CodeExecutionLogRepository executionLogRepository;
    
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    
    //-------------------------------------------------------------------------
    // 코딩 문제 관련 기능
    //-------------------------------------------------------------------------
    
    @Override
    @Transactional(readOnly = true)
    public Page<CodingProblem> getProblems(CodingProblem.Difficulty difficulty, String category, Pageable pageable) {
        return problemRepository.findByFilters(difficulty, category, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CodingProblem> searchProblems(String keyword, Pageable pageable) {
        return problemRepository.searchByKeyword(keyword, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CodingProblem getProblem(Long problemId) {
        return problemRepository.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다: " + problemId));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TestCase> getSampleTestCases(Long problemId) {
        return testCaseRepository.findByProblemIdAndIsSampleTrue(problemId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TestCase> getPublicTestCases(Long problemId) {
        return testCaseRepository.findByProblemIdAndIsHiddenFalse(problemId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TestCase> getAllTestCases(Long problemId) {
        return testCaseRepository.findByProblemIdOrderByTestNumber(problemId);
    }
    
    @Override
    @Transactional
    public CodingProblem createProblem(
            String title,
            String description,
            String inputDescription,
            String outputDescription,
            String constraints,
            CodingProblem.Difficulty difficulty,
            List<String> categories,
            Integer timeLimitSeconds,
            Integer memoryLimitMb,
            String sampleCode) {
        
        CodingProblem problem = CodingProblem.builder()
                .title(title)
                .description(description)
                .inputDescription(inputDescription)
                .outputDescription(outputDescription)
                .constraints(constraints)
                .difficulty(difficulty)
                .categories(categories)
                .timeLimitSeconds(timeLimitSeconds)
                .memoryLimitMb(memoryLimitMb)
                .sampleCode(sampleCode)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isActive(true)
                .build();
        
        return problemRepository.save(problem);
    }
    
    @Override
    @Transactional
    public TestCase createTestCase(
            Long problemId,
            String input,
            String expectedOutput,
            boolean isSample,
            boolean isHidden,
            String explanation,
            Integer testNumber) {
        
        CodingProblem problem = getProblem(problemId);
        
        TestCase testCase = TestCase.builder()
                .problem(problem)
                .input(input)
                .expectedOutput(expectedOutput)
                .isSample(isSample)
                .isHidden(isHidden)
                .explanation(explanation)
                .testNumber(testNumber)
                .build();
        
        return testCaseRepository.save(testCase);
    }
    
    @Override
    @Transactional
    public void updateProblem(Long problemId, CodingProblem updatedProblem) {
        CodingProblem problem = getProblem(problemId);
        
        // Problem 엔티티의 필드를 리플렉션을 사용해 업데이트
        try {
            if (updatedProblem.getTitle() != null) {
                problem.setTitle(updatedProblem.getTitle());
            }
            
            if (updatedProblem.getDescription() != null) {
                problem.setDescription(updatedProblem.getDescription());
            }
            
            if (updatedProblem.getInputDescription() != null) {
                problem.setInputDescription(updatedProblem.getInputDescription());
            }
            
            if (updatedProblem.getOutputDescription() != null) {
                problem.setOutputDescription(updatedProblem.getOutputDescription());
            }
            
            if (updatedProblem.getConstraints() != null) {
                problem.setConstraints(updatedProblem.getConstraints());
            }
            
            if (updatedProblem.getDifficulty() != null) {
                problem.setDifficulty(updatedProblem.getDifficulty());
            }
            
            if (updatedProblem.getCategories() != null) {
                problem.setCategories(updatedProblem.getCategories());
            }
            
            if (updatedProblem.getTimeLimitSeconds() != null) {
                problem.setTimeLimitSeconds(updatedProblem.getTimeLimitSeconds());
            }
            
            if (updatedProblem.getMemoryLimitMb() != null) {
                problem.setMemoryLimitMb(updatedProblem.getMemoryLimitMb());
            }
            
            if (updatedProblem.getSampleCode() != null) {
                problem.setSampleCode(updatedProblem.getSampleCode());
            }
            
            // 업데이트 시간 갱신
            problem.setUpdatedAt(LocalDateTime.now());
            
        } catch (Exception e) {
            log.error("문제 업데이트 중 오류 발생", e);
            throw new RuntimeException("문제 업데이트 실패", e);
        }
        
        problemRepository.save(problem);
    }
    
    @Override
    @Transactional
    public void deactivateProblem(Long problemId) {
        CodingProblem problem = getProblem(problemId);
        // 빌더 패턴으로 새 객체를 만들어 대체
        CodingProblem updatedProblem = CodingProblem.builder()
            .id(problem.getId())
            .title(problem.getTitle())
            .description(problem.getDescription())
            .inputDescription(problem.getInputDescription())
            .outputDescription(problem.getOutputDescription())
            .constraints(problem.getConstraints())
            .difficulty(problem.getDifficulty())
            .categories(problem.getCategories())
            .timeLimitSeconds(problem.getTimeLimitSeconds())
            .memoryLimitMb(problem.getMemoryLimitMb())
            .sampleCode(problem.getSampleCode())
            .testCases(problem.getTestCases())
            .createdAt(problem.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .isActive(false)
            .build();
        problemRepository.save(updatedProblem);
    }
    
    //-------------------------------------------------------------------------
    // 코드 실행 관련 기능
    //-------------------------------------------------------------------------
    
    @Override
    @Transactional
    public CodeExecutionResult executeCode(String code, String language, String input) {
        // 실행을 위한 임시 세션 및 파일 생성
        IdeSession session = createTemporarySession();
        IdeFile file = createTemporaryFile(session, code, language);
        
        try {
            // 코드 실행 로그 생성
            CodeExecutionLog executionLog = CodeExecutionLog.builder()
                    .session(session)
                    .file(file)
                    .executionType(CodeExecutionLog.ExecutionType.RUN)
                    .startedAt(LocalDateTime.now())
                    .build();
            
            executionLogRepository.save(executionLog);
            
            // 코드 실행 로직
            // 실제 구현에서는 Docker 등을 통해 샌드박스 환경에서 안전하게 실행해야 함
            String output;
            String error = null;
            Long executionTime = 0L;
            Float memoryUsed = 0f;
            
            try {
                // 코드 실행 로직 (예시)
                ProcessBuilder processBuilder = getProcessBuilderForLanguage(code, language);
                Process process = processBuilder.start();
                
                if (input != null && !input.isEmpty()) {
                    process.getOutputStream().write(input.getBytes());
                    process.getOutputStream().flush();
                    process.getOutputStream().close();
                }
                
                // 제한 시간 설정
                boolean completed = process.waitFor(30, java.util.concurrent.TimeUnit.SECONDS);
                
                if (!completed) {
                    process.destroyForcibly();
                    throw new RuntimeException("코드 실행 시간이 초과되었습니다.");
                }
                
                // 실행 결과 읽기
                try (java.util.Scanner inputScanner = new java.util.Scanner(process.getInputStream()).useDelimiter("\\A")) {
                    output = inputScanner.hasNext() ? inputScanner.next() : "";
                }
                
                // 오류 읽기
                try (java.util.Scanner errorScanner = new java.util.Scanner(process.getErrorStream()).useDelimiter("\\A")) {
                    error = errorScanner.hasNext() ? errorScanner.next() : null;
                }
                
                executionTime = 100L; // 실제로는 측정해야 함
                memoryUsed = 50.0f; // 실제로는 측정해야 함
            } catch (Exception e) {
                log.error("코드 실행 중 오류 발생", e);
                output = null;
                error = e.getMessage();
            }
            
            // 실행 로그 업데이트
            executionLog.setEndedAt(LocalDateTime.now());
            executionLog.setStdout(output);
            executionLog.setStderr(error);
            executionLog.setMemoryUsedMb(memoryUsed);
            executionLogRepository.save(executionLog);
            
            // 결과 반환
            return CodeExecutionResult.builder()
                    .success(error == null || error.isEmpty())
                    .output(output)
                    .error(error)
                    .executionTime(executionTime)
                    .memoryUsed(memoryUsed)
                    .build();
            
        } catch (Exception e) {
            log.error("코드 실행 처리 중 오류 발생", e);
            
            // 실행 로그 생성 (실패)
            CodeExecutionLog executionLog = CodeExecutionLog.builder()
                    .session(session)
                    .file(file)
                    .executionType(CodeExecutionLog.ExecutionType.RUN)
                    .startedAt(LocalDateTime.now())
                    .endedAt(LocalDateTime.now())
                    .stderr(e.getMessage())
                    .build();
            
            executionLogRepository.save(executionLog);
            
            return CodeExecutionResult.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build();
        }
    }
    
    @Override
    @Transactional
    public CodeExecutionResult executeCodeWithTestCase(String code, String language, Long testCaseId) {
        TestCase testCase = testCaseRepository.findById(testCaseId)
                .orElseThrow(() -> new IllegalArgumentException("테스트 케이스를 찾을 수 없습니다: " + testCaseId));
        
        return executeCode(code, language, testCase.getInput());
    }
    
    /**
     * 언어별 프로세스 빌더 반환
     */
    private ProcessBuilder getProcessBuilderForLanguage(String code, String language) throws Exception {
        switch (language.toLowerCase()) {
            case "java":
                // 자바 코드 실행 (예시)
                return new ProcessBuilder("java", "-cp", ".", "Main");
            case "python":
                return new ProcessBuilder("python", "-c", code);
            case "javascript":
                return new ProcessBuilder("node", "-e", code);
            case "cpp":
                // C++ 코드는 먼저 컴파일해야 함
                throw new UnsupportedOperationException("C++ 실행은 현재 지원되지 않습니다");
            default:
                throw new UnsupportedOperationException("지원되지 않는 언어입니다: " + language);
        }
    }
    
    //-------------------------------------------------------------------------
    // 문제 제출 관련 기능
    //-------------------------------------------------------------------------
    
    @Override
    @Transactional
    public Long submitProblem(User user, Long problemId, String code, String language) {
        CodingProblem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다: " + problemId));
        
        ProblemSubmission submission = ProblemSubmission.builder()
                .user(user)
                .problem(problem)
                .submittedCode(code)
                .language(ProblemSubmission.ProgrammingLanguage.valueOf(language.toUpperCase()))
                .status(ProblemSubmission.SubmissionStatus.PENDING)
                .submittedAt(LocalDateTime.now())
                .build();
        
        ProblemSubmission savedSubmission = submissionRepository.save(submission);
        
        // 비동기적으로 제출 평가 시작
        CompletableFuture.runAsync(() -> evaluateSubmission(savedSubmission.getId()), executorService);
        
        return savedSubmission.getId();
    }
    
    @Override
    @Transactional
    public void evaluateSubmission(Long submissionId) {
        ProblemSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("제출을 찾을 수 없습니다: " + submissionId));
        
        try {
            // 상태 업데이트 - 실행 중
            submission.setStatus(ProblemSubmission.SubmissionStatus.RUNNING);
            submissionRepository.save(submission);
            
            CodingProblem problem = submission.getProblem();
            List<TestCase> testCases = testCaseRepository.findByProblemIdOrderByTestNumber(problem.getId());
            
            List<ProblemSubmission.TestCaseResult> testResults = new ArrayList<>();
            boolean allPassed = true;
            int totalScore = 0;
            int maxExecutionTime = 0;
            float maxMemoryUsed = 0f;
            
            // 각 테스트 케이스에 대해 코드 실행
            for (TestCase testCase : testCases) {
                CodeExecutionResult result = executeCode(
                        submission.getSubmittedCode(),
                        submission.getLanguage().name().toLowerCase(),
                        testCase.getInput()
                );
                
                // 출력 결과 비교
                boolean passed = result.isSuccess() && result.getOutput() != null && 
                        normalizeOutput(result.getOutput()).equals(normalizeOutput(testCase.getExpectedOutput()));
                
                if (!passed) {
                    allPassed = false;
                }
                
                // 테스트 결과 생성
                ProblemSubmission.TestCaseResult testResult = ProblemSubmission.TestCaseResult.builder()
                        .testCaseId(testCase.getId())
                        .passed(passed)
                        .executionTimeMs(result.getExecutionTime() != null ? result.getExecutionTime().intValue() : null)
                        .memoryUsedMb(result.getMemoryUsed())
                        .actualOutput(result.getOutput())
                        .errorMessage(result.getError())
                        .build();
                
                testResults.add(testResult);
                
                // 최대 실행시간 및 메모리 사용량 업데이트
                if (result.getExecutionTime() != null && result.getExecutionTime() > maxExecutionTime) {
                    maxExecutionTime = result.getExecutionTime().intValue();
                }
                
                if (result.getMemoryUsed() != null && result.getMemoryUsed() > maxMemoryUsed) {
                    maxMemoryUsed = result.getMemoryUsed();
                }
                
                // 메모리/시간 제한 초과 체크
                if (result.getExecutionTime() != null && result.getExecutionTime() > problem.getTimeLimitSeconds() * 1000L) {
                    updateSubmissionStatusWithDetails(
                            submission,
                            ProblemSubmission.SubmissionStatus.TIME_LIMIT_EXCEEDED,
                            0,
                            maxExecutionTime,
                            maxMemoryUsed,
                            testResults
                    );
                    return;
                }
                
                if (result.getMemoryUsed() != null && result.getMemoryUsed() > problem.getMemoryLimitMb()) {
                    updateSubmissionStatusWithDetails(
                            submission,
                            ProblemSubmission.SubmissionStatus.MEMORY_LIMIT_EXCEEDED,
                            0,
                            maxExecutionTime,
                            maxMemoryUsed,
                            testResults
                    );
                    return;
                }
            }
            
            // 모든 테스트 통과 시 점수 계산 (100점 만점)
            if (allPassed) {
                totalScore = 100;
                updateSubmissionStatusWithDetails(
                        submission,
                        ProblemSubmission.SubmissionStatus.ACCEPTED,
                        totalScore,
                        maxExecutionTime,
                        maxMemoryUsed,
                        testResults
                );
            } else {
                // 일부 테스트만 통과한 경우
                long passedCount = testResults.stream().filter(ProblemSubmission.TestCaseResult::getPassed).count();
                totalScore = (int) ((double) passedCount / testResults.size() * 100);
                
                updateSubmissionStatusWithDetails(
                        submission,
                        ProblemSubmission.SubmissionStatus.WRONG_ANSWER,
                        totalScore,
                        maxExecutionTime,
                        maxMemoryUsed,
                        testResults
                );
            }
            
        } catch (Exception e) {
            log.error("제출 평가 중 오류 발생: submissionId={}", submissionId, e);
            
            // 컴파일/런타임 오류로 처리
            if (e.getMessage().contains("compile")) {
                submission.setStatus(ProblemSubmission.SubmissionStatus.COMPILATION_ERROR);
            } else {
                submission.setStatus(ProblemSubmission.SubmissionStatus.RUNTIME_ERROR);
            }
            submission.setCompletedAt(LocalDateTime.now());
            submissionRepository.save(submission);
        }
    }
    
    private void updateSubmissionStatusWithDetails(
            ProblemSubmission submission,
            ProblemSubmission.SubmissionStatus status,
            Integer score,
            Integer executionTimeMs,
            Float memoryUsedMb,
            List<ProblemSubmission.TestCaseResult> testResults) {
        
        submission.setStatus(status);
        submission.setCompletedAt(LocalDateTime.now());
        submission.setScore(score);
        submission.setExecutionTimeMs(executionTimeMs);
        submission.setMemoryUsedMb(memoryUsedMb);
        submission.setTestCaseResults(testResults);
        
        submissionRepository.save(submission);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SubmissionResponse getSubmissionResult(Long submissionId, User user) {
        ProblemSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("제출을 찾을 수 없습니다: " + submissionId));
        
        // 권한 확인 - 자신의 제출 또는 관리자만 조회 가능
        if (!submission.getUser().getId().equals(user.getId()) && !user.getRole().name().equals("ADMIN")) {
            throw new AccessDeniedException("이 제출에 대한 접근 권한이 없습니다");
        }
        
        return convertToSubmissionResponse(submission);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<SubmissionResponse> getUserSubmissions(User user, Long problemId, Pageable pageable) {
        Page<ProblemSubmission> submissions;
        
        if (problemId != null) {
            submissions = submissionRepository.findByUserAndProblemIdOrderBySubmittedAtDesc(user, problemId, pageable);
        } else {
            submissions = submissionRepository.findByUserOrderBySubmittedAtDesc(user, pageable);
        }
        
        return submissions.map(this::convertToSubmissionResponse);
    }
    
    private SubmissionResponse convertToSubmissionResponse(ProblemSubmission submission) {
        List<SubmissionResponse.TestResultResponse> testResults = submission.getTestCaseResults().stream()
                .map(result -> SubmissionResponse.TestResultResponse.builder()
                        .testCaseId(result.getTestCaseId())
                        .passed(result.getPassed())
                        .executionTimeMs(result.getExecutionTimeMs())
                        .memoryUsedMb(result.getMemoryUsedMb())
                        .actualOutput(result.getActualOutput())
                        .errorMessage(result.getErrorMessage())
                        .build())
                .collect(Collectors.toList());
        
        return SubmissionResponse.builder()
                .submissionId(submission.getId())
                .problemId(submission.getProblem().getId())
                .status(submission.getStatus().name())
                .score(submission.getScore())
                .language(submission.getLanguage().name())
                .executionTimeMs(submission.getExecutionTimeMs())
                .memoryUsedMb(submission.getMemoryUsedMb())
                .submittedAt(submission.getSubmittedAt())
                .completedAt(submission.getCompletedAt())
                .code(submission.getSubmittedCode())
                .testResults(testResults)
                .build();
    }
    
    //-------------------------------------------------------------------------
    // 유틸리티 메소드
    //-------------------------------------------------------------------------
    
    /**
     * 코드 실행용 임시 세션 생성
     */
    private IdeSession createTemporarySession() {
        // 테스트용 임시 세션 생성
        IdeSession session = IdeSession.builder()
                .sessionId(UUID.randomUUID().toString())
                .startedAt(LocalDateTime.now())
                .build();
        
        return sessionRepository.save(session);
    }
    
    /**
     * 코드 실행용 임시 파일 생성
     */
    private IdeFile createTemporaryFile(IdeSession session, String code, String language) {
        String fileExtension = getFileExtensionForLanguage(language);
        String filename = "Main" + fileExtension;
        
        IdeFile file = IdeFile.builder()
                .session(session)
                .filename(filename)
                .content(code)
                .updatedAt(LocalDateTime.now())
                .build();
        
        return fileRepository.save(file);
    }
    
    /**
     * 언어에 따른 파일 확장자 반환
     */
    private String getFileExtensionForLanguage(String language) {
        switch (language.toLowerCase()) {
            case "java":
                return ".java";
            case "python":
                return ".py";
            case "javascript":
                return ".js";
            case "cpp":
                return ".cpp";
            case "go":
                return ".go";
            default:
                return ".txt";
        }
    }
    
    /**
     * 출력 문자열 정규화
     */
    private String normalizeOutput(String output) {
        if (output == null) return "";
        
        // 여러 줄 끝에 있는 공백 제거 및 줄바꿈 표준화
        return output.trim()
                .replaceAll("\\r\\n", "\n")
                .replaceAll("\\r", "\n")
                .replaceAll("\\s+$", "")
                .replaceAll(" +$", "")
                .trim();
    }
}
