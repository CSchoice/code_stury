package stquokka.codeStudy.domain.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationKafkaMessage {
    private String userId;
    private String title;
    private String content;
    private String type;
    private Long timestamp;
}
