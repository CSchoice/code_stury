package stquokka.codeStudy.domain.ide.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import stquokka.codeStudy.domain.ide.entity.CodeExecutionLog;
import stquokka.codeStudy.domain.ide.entity.IdeFile;
import stquokka.codeStudy.domain.ide.entity.IdeSession;

import java.util.List;

public interface CodeExecutionLogRepository extends JpaRepository<CodeExecutionLog, Long> {
    
    List<CodeExecutionLog> findBySessionOrderByStartedAtDesc(IdeSession session);
    
    List<CodeExecutionLog> findByFileOrderByStartedAtDesc(IdeFile file);
    
    Page<CodeExecutionLog> findBySessionOrderByStartedAtDesc(IdeSession session, Pageable pageable);
}
