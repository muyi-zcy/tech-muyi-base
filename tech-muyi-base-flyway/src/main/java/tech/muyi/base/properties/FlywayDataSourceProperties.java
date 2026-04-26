package tech.muyi.base.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Flyway 数据源配置。
 *
 * <p>复用 `spring.datasource` 配置，保证迁移连接与业务连接一致。</p>
 */
@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
@Data
public class FlywayDataSourceProperties {



    private String url;

    private String username;

    private String password;

    private String driverClassName;


}
