package tech.muyi.sso;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.redisson.spring.starter.RedissonProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.muyi.redis.RedissonManage;
import tech.muyi.sso.properties.MySsoProperties;

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

    @Bean
    public MySsoManager mySsoManager() {
        return new MySsoManager(mySsoProperties);
    }
}
