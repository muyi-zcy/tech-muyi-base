package tech.muyi.db.config;

import org.apache.shardingsphere.spi.NewInstanceServiceLoader;
import org.apache.shardingsphere.underlying.executor.hook.SQLExecutionHook;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.muyi.core.MyTracer;
import tech.muyi.core.config.MyLogAutoConfiguration;
import tech.muyi.db.OpenTracingSQLExecutionHook;
import tech.muyi.db.config.properties.MyLogDbProperties;

/**
 * @author: muyi
 * @date: 2023/1/18
 **/

@Configuration
@EnableConfigurationProperties({MyLogDbProperties.class})
@ConditionalOnWebApplication
@AutoConfigureAfter({MyLogAutoConfiguration.class, MyTracer.class})
@ConditionalOnProperty(prefix = "muyi.log.db", value = "enable", matchIfMissing = true)
public class MyLogDbAutoConfiguration {

}
