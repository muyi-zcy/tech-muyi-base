package tech.muyi.springmvc.config;

import com.alipay.sofa.tracer.plugins.springmvc.SpringMvcSofaTracerFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringMVC Tracer 自动配置
 * 包装官方 sofa-tracer-springmvc-plugin，提供统一配置
 *
 * @author muyi
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(SpringMvcSofaTracerFilter.class)
@EnableConfigurationProperties(SpringMvcTracerProperties.class)
@ConditionalOnProperty(
        prefix = "tech.muyi.tracer.springmvc",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class SpringMvcTracerAutoConfiguration {

    @Bean
    public FilterRegistrationBean<SpringMvcSofaTracerFilter> springMvcSofaTracerFilter(SpringMvcTracerProperties properties) {
        FilterRegistrationBean<SpringMvcSofaTracerFilter> registration = new FilterRegistrationBean<>();

        SpringMvcSofaTracerFilter filter = new SpringMvcSofaTracerFilter();
        registration.setFilter(filter);

        // 设置 URL 匹配模式
        String[] urlPatterns = properties.getUrlPatterns().split(",");
        for (String pattern : urlPatterns) {
            registration.addUrlPatterns(pattern.trim());
        }

        // 设置过滤器顺序
        registration.setOrder(properties.getFilterOrder());
        registration.setName("springMvcSofaTracerFilter");

        return registration;
    }
}
