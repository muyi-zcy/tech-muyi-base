package tech.muyi.id.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

/**
 * @author: muyi
 * @date: 2023/1/10
 **/

@ConfigurationProperties(
        prefix = "muyi.id"
)
@Configuration
@Data
public class MyIdProperties {

    @NestedConfigurationProperty
    private MyIdSnowflakeProperties snowflake;
}
