package tech.muyi.dubbo.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Dubbo Tracer 自动配置
 * 包装官方 sofa-tracer-dubbo-plugin，提供统一配置
 *
 * @author muyi
 */
@Configuration
@ConditionalOnClass(name = "com.alipay.sofa.tracer.plugins.dubbo.DubboSofaTracerFilter")
@EnableConfigurationProperties(DubboTracerProperties.class)
public class DubboTracerAutoConfiguration {

    @Bean
    @ConditionalOnProperty(
            prefix = "tech.muyi.tracer.dubbo",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public DubboTracerInitializer dubboTracerInitializer(DubboTracerProperties properties) {

        // 官方插件会自动通过 Dubbo SPI 加载
        // 这里只需要设置我们的自定义配置

        return new DubboTracerInitializer(properties);
    }

    /**
     * Tracer 初始化器
     */
    public static class DubboTracerInitializer {
        private final DubboTracerProperties properties;

        public DubboTracerInitializer(DubboTracerProperties properties) {
            this.properties = properties;
        }

        public DubboTracerProperties getProperties() {
            return properties;
        }
    }
}
