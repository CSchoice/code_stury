package stquokka.codeStudy.domain.ide.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stquokka.codeStudy.domain.ide.entity.IdeSession;
import stquokka.codeStudy.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface IdeSessionRepository extends JpaRepository<IdeSession, Long> {
    
    List<IdeSession> findByUserOrderByStartedAtDesc(User user);
    
    Optional<IdeSession> findByIdAndUser(Long id, User user);
}
