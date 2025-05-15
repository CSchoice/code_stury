package stquokka.codeStudy.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stquokka.codeStudy.domain.user.common.PasswordHistory;

import java.util.List;

public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long> {
    List<PasswordHistory> getHistoriesByUserId(Long userId);
}
