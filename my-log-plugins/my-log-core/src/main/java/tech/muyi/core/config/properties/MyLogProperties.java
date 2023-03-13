package tech.muyi.core.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * @author: muyi
 * @date: 2022/12/29
 **/
@ConfigurationProperties(
        prefix = "muyi.log"
)
@Configuration
@Data
public class MyLogProperties {
    private boolean enable  = true;
    private HashMap<String,String> tags;

}
