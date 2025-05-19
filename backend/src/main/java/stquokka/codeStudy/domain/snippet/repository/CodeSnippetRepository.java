package stquokka.codeStudy.domain.snippet.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import stquokka.codeStudy.domain.snippet.entity.CodeSnippet;
import stquokka.codeStudy.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface CodeSnippetRepository extends JpaRepository<CodeSnippet, Long> {
    
    Page<CodeSnippet> findByOwnerOrderByCreatedAtDesc(User owner, Pageable pageable);
    
    Page<CodeSnippet> findByIsPublicTrueOrderByCreatedAtDesc(Pageable pageable);
    
    @Query("SELECT s FROM CodeSnippet s WHERE s.isPublic = true AND " +
            "(LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<CodeSnippet> searchPublicSnippets(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT s FROM CodeSnippet s JOIN s.tags t WHERE s.isPublic = true AND LOWER(t) = LOWER(:tag)")
    Page<CodeSnippet> findByTag(@Param("tag") String tag, Pageable pageable);
    
    Optional<CodeSnippet> findByShareToken(String shareToken);
    
    @Query("SELECT s FROM CodeSnippet s WHERE s.isPublic = true AND " +
            "s.language = :language ORDER BY s.likeCount DESC, s.viewCount DESC")
    Page<CodeSnippet> findPopularByLanguage(@Param("language") String language, Pageable pageable);
    
    @Query(value = "SELECT DISTINCT s.language FROM CodeSnippet s WHERE s.isPublic = true")
    List<String> findAllLanguages();
}
