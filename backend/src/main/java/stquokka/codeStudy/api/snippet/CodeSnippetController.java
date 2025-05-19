package stquokka.codeStudy.api.snippet;

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
import stquokka.codeStudy.api.snippet.request.SnippetCreateRequest;
import stquokka.codeStudy.api.snippet.request.SnippetUpdateRequest;
import stquokka.codeStudy.api.snippet.response.SnippetResponse;
import stquokka.codeStudy.domain.snippet.entity.CodeSnippet;
import stquokka.codeStudy.domain.snippet.service.CodeSnippetService;
import stquokka.codeStudy.domain.user.entity.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/snippets")
@Tag(name = "Code Snippets", description = "코드 스니펫 관리")
public class CodeSnippetController {

    private final CodeSnippetService snippetService;

    @Operation(summary = "스니펫 생성", description = "새로운 코드 스니펫을 생성합니다")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SnippetResponse> createSnippet(
            @Validated @RequestBody SnippetCreateRequest request,
            @AuthenticationPrincipal User user) {

        log.info("사용자 [{}]가 코드 스니펫 생성 요청", user.getEmail());

        CodeSnippet snippet = snippetService.createSnippet(
                user,
                request.getTitle(),
                request.getCode(),
                request.getLanguage(),
                request.getDescription(),
                request.getIsPublic(),
                request.getTags()
        );

        return ResponseEntity.ok(convertToResponse(snippet));
    }

    @Operation(summary = "내 스니펫 목록 조회", description = "사용자의 코드 스니펫 목록을 조회합니다")
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<SnippetResponse>> getMySnippets(
            Pageable pageable,
            @AuthenticationPrincipal User user) {

        Page<CodeSnippet> snippets = snippetService.getUserSnippets(user, pageable);
        Page<SnippetResponse> response = snippets.map(this::convertToResponse);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "공개 스니펫 목록 조회", description = "공개된 코드 스니펫 목록을 조회합니다")
    @GetMapping("/public")
    public ResponseEntity<Page<SnippetResponse>> getPublicSnippets(Pageable pageable) {
        Page<CodeSnippet> snippets = snippetService.getPublicSnippets(pageable);
        Page<SnippetResponse> response = snippets.map(this::convertToResponse);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "스니펫 검색", description = "키워드로 코드 스니펫을 검색합니다")
    @GetMapping("/search")
    public ResponseEntity<Page<SnippetResponse>> searchSnippets(
            @RequestParam String keyword,
            Pageable pageable) {

        Page<CodeSnippet> snippets = snippetService.searchSnippets(keyword, pageable);
        Page<SnippetResponse> response = snippets.map(this::convertToResponse);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "태그별 스니펫 조회", description = "특정 태그가 붙은 코드 스니펫을 조회합니다")
    @GetMapping("/tags/{tag}")
    public ResponseEntity<Page<SnippetResponse>> getSnippetsByTag(
            @PathVariable String tag,
            Pageable pageable) {

        Page<CodeSnippet> snippets = snippetService.getSnippetsByTag(tag, pageable);
        Page<SnippetResponse> response = snippets.map(this::convertToResponse);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "스니펫 상세 조회", description = "코드 스니펫의 상세 정보를 조회합니다")
    @GetMapping("/{snippetId}")
    public ResponseEntity<SnippetResponse> getSnippet(@PathVariable Long snippetId) {
        CodeSnippet snippet = snippetService.getSnippet(snippetId);
        snippetService.incrementViewCount(snippetId);

        return ResponseEntity.ok(convertToResponse(snippet));
    }

    @Operation(summary = "스니펫 업데이트", description = "코드 스니펫 정보를 수정합니다")
    @PutMapping("/{snippetId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SnippetResponse> updateSnippet(
            @PathVariable Long snippetId,
            @RequestBody SnippetUpdateRequest request,
            @AuthenticationPrincipal User user) {

        log.info("사용자 [{}]가 스니펫 [{}] 업데이트 요청", user.getEmail(), snippetId);

        CodeSnippet snippet = snippetService.updateSnippet(
                snippetId,
                user,
                request.getTitle(),
                request.getCode(),
                request.getLanguage(),
                request.getDescription(),
                request.getIsPublic(),
                request.getTags()
        );

        return ResponseEntity.ok(convertToResponse(snippet));
    }

    @Operation(summary = "스니펫 삭제", description = "코드 스니펫을 삭제합니다")
    @DeleteMapping("/{snippetId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteSnippet(
            @PathVariable Long snippetId,
            @AuthenticationPrincipal User user) {

        log.info("사용자 [{}]가 스니펫 [{}] 삭제 요청", user.getEmail(), snippetId);

        snippetService.deleteSnippet(snippetId, user);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "공유 토큰 생성", description = "코드 스니펫 공유를 위한 토큰을 생성합니다")
    @PostMapping("/{snippetId}/share")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> generateShareToken(
            @PathVariable Long snippetId,
            @AuthenticationPrincipal User user) {

        log.info("사용자 [{}]가 스니펫 [{}] 공유 토큰 생성 요청", user.getEmail(), snippetId);

        String shareToken = snippetService.generateShareToken(snippetId, user);

        return ResponseEntity.ok(Map.of("shareToken", shareToken));
    }

    @Operation(summary = "공유 토큰으로 조회", description = "공유 토큰을 사용하여 코드 스니펫을 조회합니다")
    @GetMapping("/shared/{shareToken}")
    public ResponseEntity<SnippetResponse> getSnippetByShareToken(
            @PathVariable String shareToken) {

        CodeSnippet snippet = snippetService.getSnippetByShareToken(shareToken);
        snippetService.incrementViewCount(snippet.getId());

        return ResponseEntity.ok(convertToResponse(snippet));
    }

    @Operation(summary = "좋아요 토글", description = "코드 스니펫 좋아요를 토글합니다")
    @PostMapping("/{snippetId}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable Long snippetId,
            @AuthenticationPrincipal User user) {

        boolean liked = snippetService.toggleLike(snippetId, user);
        CodeSnippet snippet = snippetService.getSnippet(snippetId);

        return ResponseEntity.ok(Map.of(
                "liked", liked,
                "likeCount", snippet.getLikeCount()
        ));
    }

    @Operation(summary = "인기 스니펫 조회", description = "특정 언어의 인기 코드 스니펫을 조회합니다")
    @GetMapping("/popular/{language}")
    public ResponseEntity<Page<SnippetResponse>> getPopularByLanguage(
            @PathVariable String language,
            Pageable pageable) {

        Page<CodeSnippet> snippets = snippetService.getPopularByLanguage(language, pageable);
        Page<SnippetResponse> response = snippets.map(this::convertToResponse);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "언어 목록 조회", description = "사용 가능한 모든 프로그래밍 언어 목록을 조회합니다")
    @GetMapping("/languages")
    public ResponseEntity<List<String>> getLanguages() {
        List<String> languages = snippetService.getAvailableLanguages();
        return ResponseEntity.ok(languages);
    }

    private SnippetResponse convertToResponse(CodeSnippet snippet) {
        return SnippetResponse.builder()
                .id(snippet.getId())
                .title(snippet.getTitle())
                .code(snippet.getCode())
                .language(snippet.getLanguage())
                .description(snippet.getDescription())
                .ownerId(snippet.getOwner().getId())
                .ownerUsername(snippet.getOwner().getNickname() != null
                        ? snippet.getOwner().getNickname()
                        : snippet.getOwner().getName())
                .isPublic(snippet.isPublic())
                .likeCount(snippet.getLikeCount())
                .viewCount(snippet.getViewCount())
                .tags(snippet.getTags())
                .createdAt(snippet.getCreatedAt())
                .updatedAt(snippet.getUpdatedAt())
                .build();
    }
}
