package tech.muyi.base;


import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.muyi.base.properties.FlywayDataSourceProperties;

import javax.sql.DataSource;

/**
 * Flyway 迁移自动配置。
 *
 * <p>在 `spring.flyway.enable=true` 时启用，使用业务数据源配置执行迁移脚本。</p>
 */
@Configuration
@EnableConfigurationProperties(FlywayDataSourceProperties.class)
@ConditionalOnProperty(name = {"spring.flyway.enable"}, havingValue = "true")
public class FlywayAutoConfiguration {



    @Autowired
    private FlywayDataSourceProperties flywayDataSourceProperties;




    @Bean
    public Flyway flyway() {



        DataSource dataSource = DataSourceBuilder.create()
                .url(flywayDataSourceProperties.getUrl())
                .password(flywayDataSourceProperties.getPassword())
                .username(flywayDataSourceProperties.getUsername())
                .driverClassName(flywayDataSourceProperties.getDriverClassName())
                .build();

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:flyway") // 设置迁移脚本的位置
                .baselineOnMigrate(true) // 数据库不为空
                .baselineVersion("0")// 数据库不为空设置为0
                .load();

        // 启动即执行迁移，确保应用运行前 schema 已对齐。
        flyway.migrate(); // 执行迁移
        return flyway;
    }


}
