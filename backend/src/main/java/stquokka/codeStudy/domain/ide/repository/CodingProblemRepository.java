package stquokka.codeStudy.domain.ide.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stquokka.codeStudy.domain.ide.entity.CodingProblem;

import java.util.List;

@Repository
public interface CodingProblemRepository extends JpaRepository<CodingProblem, Long> {
    
    Page<CodingProblem> findByIsActiveTrue(Pageable pageable);
    
    @Query("SELECT p FROM CodingProblem p WHERE p.isActive = true AND " +
           "(:difficulty IS NULL OR p.difficulty = :difficulty) AND " +
           "(:category IS NULL OR :category IN (SELECT c FROM p.categories c))")
    Page<CodingProblem> findByFilters(
            @Param("difficulty") CodingProblem.Difficulty difficulty,
            @Param("category") String category,
            Pageable pageable);
    
    @Query("SELECT p FROM CodingProblem p WHERE p.isActive = true AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<CodingProblem> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT DISTINCT c FROM CodingProblem p JOIN p.categories c WHERE p.isActive = true")
    List<String> findAllCategories();
}
