package tech.muyi.db.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author: muyi
 * @date: 2022/12/29
 **/
@ConfigurationProperties(
        prefix = "muyi.log.db"
)
@Configuration
@Data
public class MyLogDbProperties {
    private boolean enable = true;
}
