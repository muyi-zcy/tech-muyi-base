package tech.muyi.feign.config;

import com.alipay.sofa.tracer.plugins.springcloud.instruments.feign.SofaTracerFeignClient;
import feign.Client;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Feign Tracer 自动配置
 * 包装官方 sofa-tracer-springcloud-plugin，提供统一配置
 *
 * @author muyi
 */
@Configuration
@ConditionalOnClass(name = {"feign.Client", "com.alipay.sofa.tracer.plugins.springcloud.instruments.feign.SofaTracerFeignClient"})
@EnableConfigurationProperties(FeignTracerProperties.class)
@ConditionalOnProperty(
        prefix = "tech.muyi.tracer.feign",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@AutoConfigureBefore(FeignAutoConfiguration.class)
public class FeignTracerAutoConfiguration {

    /**
     * 注册 SofaTracerFeignClient
     * 使用 @Primary 确保优先使用带追踪的 Client
     */
    @Bean
    @Primary
    @ConditionalOnClass(name = "com.alipay.sofa.tracer.plugins.springcloud.instruments.feign.SofaTracerFeignClient")
    public Client feignClient() {
        return new SofaTracerFeignClient();
    }
}
