package tech.muyi.id.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

/**
 * ID 模块配置根节点。
 *
 * <p>绑定 `muyi.id` 前缀，当前仅包含 snowflake 子配置。</p>
 *
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
