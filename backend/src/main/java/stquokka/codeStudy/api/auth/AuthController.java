package stquokka.codeStudy.api.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import stquokka.codeStudy.api.CommonResponse;
import stquokka.codeStudy.api.auth.request.TokenRefreshRequest;
import stquokka.codeStudy.api.auth.response.AuthResponse;
import stquokka.codeStudy.domain.auth.command.LoginCommand;
import stquokka.codeStudy.domain.auth.service.AuthService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "인증 API", description = "로그인, 로그아웃, 토큰 갱신 등 인증 관련 API")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로그인", description = "사용자 아이디와 비밀번호로 로그인합니다.")
    @PostMapping("/login")
    public CommonResponse<AuthResponse> login(@Valid @RequestBody LoginCommand command) {
        log.info("[AuthController] 로그인 >>>> userId: {}", command.userId());
        AuthResponse response = authService.login(command);
        return CommonResponse.ok(response);
    }

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 이용해 새로운 액세스 토큰을 발급합니다.")
    @PostMapping("/refresh")
    public CommonResponse<AuthResponse> refresh(@Valid @RequestBody TokenRefreshRequest request) {
        log.info("[AuthController] 토큰 갱신 요청");
        AuthResponse response = authService.refresh(request);
        return CommonResponse.ok(response);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "로그아웃", description = "현재 로그인된 사용자를 로그아웃 처리합니다.")
    @PostMapping("/logout")
    public CommonResponse<Void> logout() {
        log.info("[AuthController] 로그아웃 요청");
        authService.logout();
        return CommonResponse.ok();
    }
}
