package tech.muyi.sso;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.muyi.sso.properties.MySsoProperties;
import tech.muyi.sso.properties.MyTenantProperties;

/**
 * @Author: muyi
 * @Date: 2021/1/3 22:57
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = {"muyi.sso.enable"}, havingValue = "true")
public class MySsoManageAutoConfiguration {

    @Autowired
    private MySsoProperties mySsoProperties;

    @Autowired
    private MyTenantProperties myTenantProperties;
    @Bean
    public MySsoManager mySsoManager() {
        return new MySsoManager(mySsoProperties,myTenantProperties);
    }
}
