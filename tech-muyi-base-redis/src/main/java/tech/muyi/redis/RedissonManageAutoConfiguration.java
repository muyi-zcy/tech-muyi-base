package tech.muyi.redis;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.redisson.spring.starter.RedissonProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import javax.cache.CacheManager;
import java.io.IOException;
import java.util.Objects;

/**
 * Redisson 自动配置入口。
 *
 * <p>仅在容器中缺少 {@link RedissonManage} 时注册默认实现，
 * 方便业务方通过自定义 Bean 覆盖默认行为。</p>
 *
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
            // 保留启动期可观测性，便于排查自动配置是否生效。
            log.info("配置Redisson 客户端 结束...");
        }
    }
}
