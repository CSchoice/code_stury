package stquokka.codeStudy.domain.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

// 삭제된 패키지의 상수를 대체하기 위해 내부 상수 정의

@Component
@Slf4j
public class BlacklistTokenRedisRepository extends BaseRedisRepository<String> {
    // 삭제된 상수를 대체하기 위한 내부 상수 정의
    private static final String BLACKLIST = "BLACKLIST";
    private static final String EXPIRED = "EXPIRED";
    public BlacklistTokenRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.prefix = BLACKLIST; // 내부 상수 사용
        this.ttl = 60 * 60 * 24 * 7L; // 7일
    }

    public void save(String token, Long ttl) {
        super.save(token, EXPIRED, ttl);
    }

    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(generateKeyFromId(key));
    }
}
