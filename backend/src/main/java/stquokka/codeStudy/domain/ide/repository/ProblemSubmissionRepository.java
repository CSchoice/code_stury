package stquokka.codeStudy.domain.ide.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import stquokka.codeStudy.domain.ide.entity.ProblemSubmission;
import stquokka.codeStudy.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface ProblemSubmissionRepository extends JpaRepository<ProblemSubmission, Long> {
    
    Page<ProblemSubmission> findByUserOrderBySubmittedAtDesc(User user, Pageable pageable);
    
    Page<ProblemSubmission> findByProblemIdOrderBySubmittedAtDesc(Long problemId, Pageable pageable);
    
    Page<ProblemSubmission> findByUserAndProblemIdOrderBySubmittedAtDesc(User user, Long problemId, Pageable pageable);
    
    @Query("SELECT s FROM ProblemSubmission s WHERE s.user = :user AND s.problem.id = :problemId AND s.status = 'ACCEPTED' " +
           "ORDER BY s.executionTimeMs ASC, s.memoryUsedMb ASC, s.submittedAt ASC")
    Optional<ProblemSubmission> findBestSubmission(@Param("user") User user, @Param("problemId") Long problemId);
    
    @Query("SELECT COUNT(DISTINCT s.problem.id) FROM ProblemSubmission s " +
           "WHERE s.user = :user AND s.status = 'ACCEPTED'")
    Long countSolvedProblems(@Param("user") User user);
    
    @Query("SELECT s.problem.difficulty, COUNT(DISTINCT s.problem.id) FROM ProblemSubmission s " +
           "WHERE s.user = :user AND s.status = 'ACCEPTED' " +
           "GROUP BY s.problem.difficulty")
    List<Object[]> countSolvedProblemsByDifficulty(@Param("user") User user);
}
