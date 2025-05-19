package stquokka.codeStudy.domain.snippet.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import stquokka.codeStudy.domain.snippet.entity.CodeSnippet;
import stquokka.codeStudy.domain.user.entity.User;

import java.util.List;

/**
 * 코드 스니펫 관리 서비스 인터페이스
 */
public interface CodeSnippetService {
    
    /**
     * 새로운 코드 스니펫 생성
     */
    CodeSnippet createSnippet(User owner, String title, String code, String language, 
                             String description, boolean isPublic, List<String> tags);
    
    /**
     * 코드 스니펫 조회
     */
    CodeSnippet getSnippet(Long snippetId);
    
    /**
     * 사용자의 코드 스니펫 목록 조회
     */
    Page<CodeSnippet> getUserSnippets(User user, Pageable pageable);
    
    /**
     * 공개된 코드 스니펫 목록 조회
     */
    Page<CodeSnippet> getPublicSnippets(Pageable pageable);
    
    /**
     * 키워드로 공개 스니펫 검색
     */
    Page<CodeSnippet> searchSnippets(String keyword, Pageable pageable);
    
    /**
     * 태그로 스니펫 조회
     */
    Page<CodeSnippet> getSnippetsByTag(String tag, Pageable pageable);
    
    /**
     * 스니펫 정보 업데이트
     */
    CodeSnippet updateSnippet(Long snippetId, User user, String title, String code, 
                             String language, String description, Boolean isPublic, List<String> tags);
    
    /**
     * 스니펫 삭제
     */
    void deleteSnippet(Long snippetId, User user);
    
    /**
     * 공유 토큰으로 스니펫 조회
     */
    CodeSnippet getSnippetByShareToken(String shareToken);
    
    /**
     * 스니펫 공유 토큰 생성
     */
    String generateShareToken(Long snippetId, User user);
    
    /**
     * 스니펫 조회수 증가
     */
    void incrementViewCount(Long snippetId);
    
    /**
     * 스니펫 좋아요 토글
     */
    boolean toggleLike(Long snippetId, User user);
    
    /**
     * 언어별 인기 스니펫 조회
     */
    Page<CodeSnippet> getPopularByLanguage(String language, Pageable pageable);
    
    /**
     * 사용 가능한 모든 언어 목록 조회
     */
    List<String> getAvailableLanguages();
}
