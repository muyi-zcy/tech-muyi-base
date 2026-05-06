package tech.muyi.redis.config;

import com.sofa.alipay.tracer.plugins.spring.redis.SofaTracerRCFBeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.muyi.redis.RedissonTracer;

/**
 * Redis Tracer 自动配置
 * 支持 Jedis/Lettuce（官方插件）和 Redisson（自定义实现）
 *
 * @author muyi
 */
@Configuration
@EnableConfigurationProperties(RedisTracerProperties.class)
@ConditionalOnProperty(prefix = "tech.muyi.tracer.redis", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RedisTracerAutoConfiguration {

    /**
     * Jedis/Lettuce 自动配置
     * 使用 SOFATracer 官方插件的 BeanPostProcessor
     */
    @Configuration
    @ConditionalOnClass(name = {"org.springframework.data.redis.connection.RedisConnectionFactory",
            "com.sofa.alipay.tracer.plugins.spring.redis.SofaTracerRCFBeanPostProcessor"})
    static class SpringDataRedisTracerConfiguration {

        @Bean
        public SofaTracerRCFBeanPostProcessor sofaTracerRCFBeanPostProcessor() {
            return new SofaTracerRCFBeanPostProcessor();
        }
    }

    /**
     * Redisson 自动配置
     * 需要自定义实现追踪
     */
    @Configuration
    @ConditionalOnClass(name = "org.redisson.api.RedissonClient")
    static class RedissonTracerConfiguration {

        @Bean
        public RedissonTracerInitializer redissonTracerInitializer(RedisTracerProperties properties) {
            RedissonTracer.setEnabled(properties.isEnabled());
            return new RedissonTracerInitializer();
        }

        /**
         * Redisson Tracer 初始化器
         */
        public static class RedissonTracerInitializer {
            // 占位 Bean，用于触发初始化
        }
    }
}
