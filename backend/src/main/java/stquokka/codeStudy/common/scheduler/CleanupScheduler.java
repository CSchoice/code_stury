package stquokka.codeStudy.common.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
// 존재하지 않는 패키지 참조 주석 처리
// import stquokka.codeStudy.domain.community.entity.Notification;
// import stquokka.codeStudy.domain.community.repository.NotificationRepository;

import java.time.LocalDateTime;

/**
 * 일정 주기로 실행되는 배치 작업을 처리하는 스케줄러
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class CleanupScheduler {

    // private final NotificationRepository notificationRepository;

    /**
     * 오래된 알림을 정리하는 배치 작업
     * 매일 새벽 2시에 실행
     */
    @Transactional
    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupOldNotifications() {
        // 임시로 기능 비활성화
        log.info("오래된 알림 정리 작업이 비활성화되었습니다. 필요한 엔티티와 리포지토리를 구현해주세요.");
        
        /*
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        int batchSize = 1000;
        int deletedCount = 0;
        
        while (true) {
            List<Notification> notifications = notificationRepository
                .findOldNotifications(oneMonthAgo, PageRequest.of(0, batchSize));
            
            if (notifications.isEmpty()) {
                break;
            }
            
            notificationRepository.deleteAll(notifications);
            deletedCount += notifications.size();
            
            log.info("삭제된 오래된 알림: {}", deletedCount);
        }
        */
    }
}
