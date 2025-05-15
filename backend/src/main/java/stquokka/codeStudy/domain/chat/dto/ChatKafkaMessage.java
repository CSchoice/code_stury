package stquokka.codeStudy.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatKafkaMessage {
    private String chatId;
    private String senderId;
    private String message;
    private Long timestamp;
}
