package tech.muyi.redis;

import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: muyi
 * @Date: 2021/1/3 22:57
 */
@Configuration
public class RedissonManageAutoConfiguration {
    public RedissonManageAutoConfiguration() {
    }

    @Bean
    @ConditionalOnMissingBean({RedissonManage.class})
    public RedissonManage redissonManage(RedissonClient redissonClient) {
        return new RedissonManage(redissonClient);
    }
}
