package stquokka.codeStudy.domain.algorithm.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProblemHistoryId implements Serializable {
    
    private Long userId;
    private Long problemId;
}
