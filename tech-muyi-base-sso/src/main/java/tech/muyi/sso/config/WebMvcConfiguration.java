package tech.muyi.sso.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tech.muyi.sso.interceptor.SsoInterceptor;
import tech.muyi.sso.properties.MySsoProperties;

import javax.annotation.Resource;
import java.util.List;


@Configuration
@ConditionalOnProperty(name = {"muyi.sso.enable"}, havingValue = "true")
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Resource
    private MySsoProperties mySsoProperties;

    @Resource
    private SsoInterceptor ssoInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> exclude = mySsoProperties.getExclude();
        exclude.add("/ok");
        exclude.add("/swagger-resources");
        exclude.add("/doc.html/*");
        exclude.add("/v3/*");
        registry.addInterceptor(ssoInterceptor)
                .excludePathPatterns(exclude)
                .addPathPatterns("/**");
    }
}
