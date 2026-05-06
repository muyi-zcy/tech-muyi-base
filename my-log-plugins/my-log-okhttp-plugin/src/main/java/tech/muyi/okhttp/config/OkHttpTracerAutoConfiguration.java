package tech.muyi.okhttp.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * OkHttp Tracer 自动配置
 *
 * 注意：SOFATracer 官方的 httpclient 插件只支持 Apache HttpClient，不支持 OkHttp。
 * 如果需要为 OkHttp 添加链路追踪，需要手动实现 Interceptor。
 *
 * 使用示例：
 * <pre>
 * {@code
 * @Bean
 * public OkHttpClient okHttpClient() {
 *     return new OkHttpClient.Builder()
 *         .addInterceptor(new OkHttpTracerInterceptor())  // 需要自定义实现
 *         .build();
 * }
 * }
 * </pre>
 *
 * @author muyi
 */
@Configuration
@ConditionalOnClass(name = "okhttp3.OkHttpClient")
@EnableConfigurationProperties(OkHttpTracerProperties.class)
@ConditionalOnProperty(
        prefix = "tech.muyi.tracer.okhttp",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class OkHttpTracerAutoConfiguration {

    public OkHttpTracerAutoConfiguration(OkHttpTracerProperties properties) {
        // OkHttp 追踪需要用户手动添加 Interceptor
        // 此配置类仅用于加载配置属性
    }
}
