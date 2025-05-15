package stquokka.codeStudy.domain.ide.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import stquokka.codeStudy.domain.ide.entity.TestCase;

import java.util.List;

public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
    
    List<TestCase> findByProblemId(Long problemId);
    
    List<TestCase> findByProblemIdAndIsSampleTrue(Long problemId);
    
    List<TestCase> findByProblemIdAndIsHiddenFalse(Long problemId);
    
    @Query("SELECT tc FROM TestCase tc WHERE tc.problem.id = :problemId ORDER BY tc.testNumber ASC")
    List<TestCase> findByProblemIdOrderByTestNumber(@Param("problemId") Long problemId);
}
