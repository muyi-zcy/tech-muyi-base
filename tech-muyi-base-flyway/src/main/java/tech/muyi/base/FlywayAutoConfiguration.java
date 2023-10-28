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
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .build();

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:flyway") // 设置迁移脚本的位置
                .baselineOnMigrate(true) // 数据库不为空
                .baselineVersion("0")// 数据库不为空设置为0
                .load();

        flyway.migrate(); // 执行迁移
        return flyway;
    }


}
