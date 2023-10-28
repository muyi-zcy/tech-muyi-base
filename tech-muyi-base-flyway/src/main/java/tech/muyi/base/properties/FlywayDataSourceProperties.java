package tech.muyi.base.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
@Data
public class FlywayDataSourceProperties {



    private String url;

    private String username;

    private String password;


}
