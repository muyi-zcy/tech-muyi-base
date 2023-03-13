package tech.muyi.redis.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.util.List;

/**
 * @author: muyi
 * @date: 2022/12/29
 **/
@ConfigurationProperties(
        prefix = "muyi.log.redis"
)
@Configuration
@Data
public class MyLogRedisProperties {
    private boolean enable = true;
}
