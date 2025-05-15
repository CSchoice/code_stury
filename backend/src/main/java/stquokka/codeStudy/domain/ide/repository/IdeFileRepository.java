package stquokka.codeStudy.domain.ide.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stquokka.codeStudy.domain.ide.entity.IdeFile;
import stquokka.codeStudy.domain.ide.entity.IdeSession;

import java.util.List;
import java.util.Optional;

public interface IdeFileRepository extends JpaRepository<IdeFile, Long> {
    
    List<IdeFile> findBySessionOrderByUpdatedAtDesc(IdeSession session);
    
    Optional<IdeFile> findByIdAndSession(Long id, IdeSession session);
    
    Optional<IdeFile> findBySessionAndFilename(IdeSession session, String filename);
}
