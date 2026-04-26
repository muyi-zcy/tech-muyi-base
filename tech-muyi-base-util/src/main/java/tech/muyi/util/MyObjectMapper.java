package tech.muyi.util;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * ObjectMapper 延迟获取工具。
 *
 * <p>优先从 Spring 容器拿统一配置后的 {@link ObjectMapper}；
 * 若当前线程环境不在 Spring 上下文（例如单测或早期静态初始化阶段），
 * 则回退到默认实例保证基础可用性。</p>
 */
public class MyObjectMapper {
    private static ObjectMapper objectMapper;

    public static ObjectMapper getObjectMapper() {
        if (objectMapper != null) {
            return objectMapper;
        }
        synchronized (MyObjectMapper.class) {
            try {
                objectMapper = ApplicationContextUtil.getBean(ObjectMapper.class);
            } catch (Throwable throwable) {
                // 兜底实例不含项目自定义模块，行为可能与运行时配置不一致。
                objectMapper = new ObjectMapper();
            }
            return objectMapper;
        }
    }
}
