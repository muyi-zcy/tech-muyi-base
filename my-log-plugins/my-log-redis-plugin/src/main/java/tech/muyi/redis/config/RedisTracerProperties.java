package tech.muyi.redis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Redis Tracer 配置属性
 *
 * @author muyi
 */
@ConfigurationProperties(prefix = "tech.muyi.tracer.redis")
public class RedisTracerProperties {

    /**
     * 是否启用 Redis 追踪
     */
    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
