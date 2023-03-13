package tech.muyi.redis.config;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.muyi.core.MyTracer;
import tech.muyi.core.config.MyLogAutoConfiguration;
import tech.muyi.redis.MyLogRedisBeanPostProcessor;
import tech.muyi.redis.config.properties.MyLogRedisProperties;

/**
 * @author: muyi
 * @date: 2022/12/29
 **/

@Configuration
@EnableConfigurationProperties({MyLogRedisProperties.class})
@ConditionalOnWebApplication
@AutoConfigureAfter({MyLogAutoConfiguration.class, RedissonClient.class, MyTracer.class})
@ConditionalOnProperty(prefix = "muyi.log.redis", value = "enable", matchIfMissing = true)
public class MyLogRedisAutoConfiguration {
    @Autowired
    private MyLogRedisProperties myLogRedisProperties;


    @Bean
    @ConditionalOnClass({MyTracer.class})
    MyLogRedisBeanPostProcessor myLogRedisBeanPostProcessor(MyTracer myTracer) {
        return new MyLogRedisBeanPostProcessor(myTracer);
    }

}
