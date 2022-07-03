package tech.muyi.redis;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.redisson.spring.starter.RedissonProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.cache.CacheManager;
import java.io.IOException;

/**
 * @Author: muyi
 * @Date: 2021/1/3 22:57
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({RedissonProperties.class, RedisProperties.class})
public class RedissonManageAutoConfiguration {

    public RedissonManageAutoConfiguration() {
        log.info("开始配置Redisson 客户端...");
    }

    @Bean
    @ConditionalOnMissingBean({RedissonManage.class})
    public RedissonManage redissonManage(RedissonClient redissonClient) {
        try {
            return new RedissonManage(redissonClient);
        }finally {
            log.info("配置Redisson 客户端 结束...");
        }
    }
}
