package tech.muyi.springmvc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.muyi.core.MyTracer;
import tech.muyi.core.config.MyLogAutoConfiguration;
import tech.muyi.springmvc.SpringMvcTracerClient;
import tech.muyi.springmvc.SpringMvcTracerFilter;
import tech.muyi.springmvc.config.properties.MyLogSpringMvcProperties;

import java.util.List;

/**
 * @author: muyi
 * @date: 2022/12/29
 **/

@Configuration
@EnableConfigurationProperties({MyLogSpringMvcProperties.class})
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = "muyi.log.springmvc", value = "enable", matchIfMissing = true)
@AutoConfigureAfter(MyLogAutoConfiguration.class)
public class MyLogSpringMvcAutoConfiguration {
    @Autowired
    private MyLogSpringMvcProperties myLogSpringMvcProperties;


    @Bean
    @ConditionalOnClass(MyTracer.class)
    public SpringMvcTracerClient springMvcTracerClient(MyTracer myTracer){
        return new SpringMvcTracerClient(myTracer);
    }

    @Configuration
    public class SpringMvcDelegatingFilterProxyConfiguration {
        @Bean
        @ConditionalOnClass(SpringMvcTracerClient.class)
        public FilterRegistrationBean springMvcDelegatingFilterProxy(SpringMvcTracerClient springMvcTracerClient) {
            FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
            SpringMvcTracerFilter filter = new SpringMvcTracerFilter(springMvcTracerClient);
            filterRegistrationBean.setFilter(filter);
            List<String> urlPatterns = myLogSpringMvcProperties.getUrlPatterns();
            if (urlPatterns == null || urlPatterns.size() <= 0) {
                filterRegistrationBean.addUrlPatterns("/*");
            } else {
                filterRegistrationBean.setUrlPatterns(urlPatterns);
            }
            filterRegistrationBean.setName(filter.getClass().getSimpleName());
            filterRegistrationBean.setAsyncSupported(true);
            filterRegistrationBean.setOrder(myLogSpringMvcProperties.getFilterOrder());
            return filterRegistrationBean;
        }
    }


}
