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

/**
 * 多租户 MyBatis 拦截器配置。
 *
 * <p>在 `muyi.tenant.enable=true` 时启用租户 SQL 注入能力。</p>
 */
@Configuration
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
