package stquokka.codeStudy.api.ide;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import stquokka.codeStudy.api.CommonResponse;
import stquokka.codeStudy.api.ide.request.CodeExecutionRequest;
import stquokka.codeStudy.api.ide.response.CodeExecutionResponse;
import stquokka.codeStudy.domain.ide.service.CodeExecutionResult;
import stquokka.codeStudy.domain.ide.service.IdeService;
import stquokka.codeStudy.domain.user.entity.User;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ide")
@Tag(name = "IDE", description = "코드 에디터 및 실행")
public class IdeController {
    private final IdeService ideService;

    @Operation(summary = "코드 실행", description = "코드를 실행하고 결과를 반환합니다")
    @PostMapping("/execute")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommonResponse<CodeExecutionResponse>> executeCode(
            @RequestBody CodeExecutionRequest request,
            @AuthenticationPrincipal User user) {
        
        log.info("코드 실행 요청: 사용자 = {}, 언어 = {}", 
                user != null ? user.getEmail() : "비회원", 
                request.getLanguage());
        
        CodeExecutionResult result = ideService.executeCode(
                request.getCode(),
                request.getLanguage(),
                request.getInput());
        
        CodeExecutionResponse response = CodeExecutionResponse.builder()
                .success(result.isSuccess())
                .output(result.getOutput())
                .error(result.getError())
                .executionTimeMs(result.getExecutionTime())
                .memoryUsedMb(result.getMemoryUsed())
                .build();
        
        return ResponseEntity.ok(new CommonResponse<>(response));
    }
}
