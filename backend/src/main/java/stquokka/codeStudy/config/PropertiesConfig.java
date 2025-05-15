package stquokka.codeStudy.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import stquokka.codeStudy.common.properties.*;

@Configuration
@EnableConfigurationProperties({
        CorsProperties.class,
        RedisProperties.class,
        KakaoLoginProperties.class,
        JwtProperties.class,
        S3Properties.class,
        WebClientProperties.class,
        ElasticsearchProperties.class,
        KafkaProperties.class,
        SpringSecurityProperties.class,
})
public class PropertiesConfig {
}
