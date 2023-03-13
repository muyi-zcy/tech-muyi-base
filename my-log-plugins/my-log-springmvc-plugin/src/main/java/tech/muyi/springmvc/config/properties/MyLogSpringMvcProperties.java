package tech.muyi.springmvc.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.util.List;

/**
 * @author: muyi
 * @date: 2022/12/29
 **/
@ConfigurationProperties(
        prefix = "muyi.log.springmvc"
)
@Configuration
@Data
public class MyLogSpringMvcProperties {
    private boolean enable = true;

    private int filterOrder = Ordered.HIGHEST_PRECEDENCE + 1;

    private List<String> urlPatterns;
}
