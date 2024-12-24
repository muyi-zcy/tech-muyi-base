package tech.muyi.sso.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.muyi.sso.MySsoManager;
import tech.muyi.sso.handler.MyTenantLineHandler;
import tech.muyi.sso.interceptor.MyTenantLineInnerInterceptor;
import tech.muyi.sso.properties.MyTenantProperties;

import javax.annotation.Resource;

@Configuration
@EnableConfigurationProperties({MyTenantProperties.class})
@ConditionalOnProperty(name = "muyi.tenant.enable", havingValue = "true", matchIfMissing = false)
public class TenantConfiguration {


    @Autowired
    private MyTenantProperties myTenantProperties;

    @Resource
    private MySsoManager mySsoManager;


    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MyTenantLineHandler myTenantLineHandler = new MyTenantLineHandler(mySsoManager, myTenantProperties);
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //多租户拦截器
        interceptor.addInnerInterceptor(new MyTenantLineInnerInterceptor(myTenantProperties, myTenantLineHandler));
        return interceptor;
    }
}
