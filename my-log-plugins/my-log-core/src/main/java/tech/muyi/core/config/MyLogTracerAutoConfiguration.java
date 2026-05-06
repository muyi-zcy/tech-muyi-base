package tech.muyi.core.config;

import com.alipay.common.tracer.core.configuration.SofaTracerConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyLog Tracer 自动配置
 *
 * @author muyi
 */
@Configuration
@EnableConfigurationProperties(MyLogTracerProperties.class)
@ConditionalOnProperty(prefix = "tech.muyi.tracer", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MyLogTracerAutoConfiguration {

    @Bean
    public MyLogTracerInitializer myLogTracerInitializer(MyLogTracerProperties properties) {
        // 设置全局日志路径
        if (properties.getLogPath() != null) {
            System.setProperty("logging.path", properties.getLogPath());
        }

        // 设置日志保留天数
        System.setProperty(SofaTracerConfiguration.TRACER_GLOBAL_LOG_RESERVE_DAY,
                String.valueOf(properties.getLogReserveDays()));

        // 设置采样率（通过系统属性）
        if (properties.getSamplingRate() < 1.0) {
            System.setProperty("com.alipay.sofa.tracer.samplerName", "PercentageBasedSampler");
            System.setProperty("com.alipay.sofa.tracer.samplerPercentage",
                    String.valueOf(properties.getSamplingRate() * 100));
        }

        return new MyLogTracerInitializer(properties);
    }

    /**
     * Tracer 初始化器
     */
    public static class MyLogTracerInitializer {
        private final MyLogTracerProperties properties;

        public MyLogTracerInitializer(MyLogTracerProperties properties) {
            this.properties = properties;
        }

        public MyLogTracerProperties getProperties() {
            return properties;
        }
    }
}
