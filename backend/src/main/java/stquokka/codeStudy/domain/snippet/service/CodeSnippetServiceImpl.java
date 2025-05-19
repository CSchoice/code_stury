package stquokka.codeStudy.domain.snippet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stquokka.codeStudy.domain.snippet.entity.CodeSnippet;
import stquokka.codeStudy.domain.snippet.repository.CodeSnippetRepository;
import stquokka.codeStudy.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeSnippetServiceImpl implements CodeSnippetService {
    
    private final CodeSnippetRepository snippetRepository;
    
    @Override
    @Transactional
    public CodeSnippet createSnippet(User owner, String title, String code, String language, 
                                    String description, boolean isPublic, List<String> tags) {
        
        CodeSnippet snippet = CodeSnippet.builder()
                .owner(owner)
                .title(title)
                .code(code)
                .language(language)
                .description(description)
                .isPublic(isPublic)
                .tags(tags)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        return snippetRepository.save(snippet);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CodeSnippet getSnippet(Long snippetId) {
        return snippetRepository.findById(snippetId)
                .orElseThrow(() -> new IllegalArgumentException("스니펫을 찾을 수 없습니다: " + snippetId));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CodeSnippet> getUserSnippets(User user, Pageable pageable) {
        return snippetRepository.findByOwnerOrderByCreatedAtDesc(user, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CodeSnippet> getPublicSnippets(Pageable pageable) {
        return snippetRepository.findByIsPublicTrueOrderByCreatedAtDesc(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CodeSnippet> searchSnippets(String keyword, Pageable pageable) {
        return snippetRepository.searchPublicSnippets(keyword, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CodeSnippet> getSnippetsByTag(String tag, Pageable pageable) {
        return snippetRepository.findByTag(tag, pageable);
    }
    
    @Override
    @Transactional
    public CodeSnippet updateSnippet(Long snippetId, User user, String title, String code, 
                                    String language, String description, Boolean isPublic, List<String> tags) {
        
        CodeSnippet snippet = getSnippet(snippetId);
        
        // 권한 확인 - 소유자만 수정 가능
        if (!snippet.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("이 스니펫을 수정할 권한이 없습니다");
        }
        
        if (title != null) {
            snippet.setTitle(title);
        }
        
        if (code != null) {
            snippet.setCode(code);
        }
        
        if (language != null) {
            snippet.setLanguage(language);
        }
        
        if (description != null) {
            snippet.setDescription(description);
        }
        
        if (isPublic != null) {
            snippet.setPublic(isPublic);
        }
        
        if (tags != null) {
            snippet.setTags(tags);
        }
        
        snippet.setUpdatedAt(LocalDateTime.now());
        
        return snippetRepository.save(snippet);
    }
    
    @Override
    @Transactional
    public void deleteSnippet(Long snippetId, User user) {
        CodeSnippet snippet = getSnippet(snippetId);
        
        // 권한 확인 - 소유자만 삭제 가능
        if (!snippet.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("이 스니펫을 삭제할 권한이 없습니다");
        }
        
        snippetRepository.delete(snippet);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CodeSnippet getSnippetByShareToken(String shareToken) {
        return snippetRepository.findByShareToken(shareToken)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 공유 토큰입니다"));
    }
    
    @Override
    @Transactional
    public String generateShareToken(Long snippetId, User user) {
        CodeSnippet snippet = getSnippet(snippetId);
        
        // 권한 확인 - 소유자만 공유 토큰 생성 가능
        if (!snippet.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("이 스니펫의 공유 토큰을 생성할 권한이 없습니다");
        }
        
        // 토큰 생성
        String shareToken = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        snippet.setShareToken(shareToken);
        snippetRepository.save(snippet);
        
        return shareToken;
    }
    
    @Override
    @Transactional
    public void incrementViewCount(Long snippetId) {
        CodeSnippet snippet = getSnippet(snippetId);
        snippet.incrementViewCount();
        snippetRepository.save(snippet);
    }
    
    @Override
    @Transactional
    public boolean toggleLike(Long snippetId, User user) {
        // 실제 구현에서는 Like 엔티티를 추가하여 좋아요 상태를 관리해야 함
        // 임시 구현에서는 간단히 좋아요 수만 증가/감소 (추후 개선 필요)
        CodeSnippet snippet = getSnippet(snippetId);
        
        boolean liked = true; // 좋아요 눌렀다 가정
        if (liked) {
            snippet.incrementLikeCount();
        } else {
            snippet.decrementLikeCount();
        }
        
        snippetRepository.save(snippet);
        return liked;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CodeSnippet> getPopularByLanguage(String language, Pageable pageable) {
        return snippetRepository.findPopularByLanguage(language, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<String> getAvailableLanguages() {
        return snippetRepository.findAllLanguages();
    }
}
