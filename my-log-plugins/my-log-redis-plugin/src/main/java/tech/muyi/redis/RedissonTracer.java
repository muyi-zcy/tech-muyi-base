package tech.muyi.redis;

import com.alipay.common.tracer.core.SofaTracer;

/**
 * Redisson Tracer 单例管理
 *
 * @author muyi
 */
public class RedissonTracer {

    private static final String TRACER_TYPE = "RedissonTracer";
    private static volatile SofaTracer sofaTracer;
    private static volatile boolean enabled = true;

    /**
     * 获取 SofaTracer 实例
     */
    public static SofaTracer getSofaTracer() {
        if (!enabled) {
            return null;
        }

        if (sofaTracer == null) {
            synchronized (RedissonTracer.class) {
                if (sofaTracer == null) {
                    sofaTracer = buildTracer();
                }
            }
        }
        return sofaTracer;
    }

    /**
     * 构建 SofaTracer 实例
     */
    private static SofaTracer buildTracer() {
        return new SofaTracer.Builder(TRACER_TYPE)
                .withTag("component", "Redisson")
                .build();
    }

    /**
     * 设置是否启用追踪
     */
    public static void setEnabled(boolean enabled) {
        RedissonTracer.enabled = enabled;
    }

    /**
     * 重置 Tracer
     */
    public static void reset() {
        sofaTracer = null;
    }
}
