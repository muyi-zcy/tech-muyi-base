package tech.muyi.mq;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author: muyi
 * @date: 2022/12/3
 **/
@ConfigurationProperties(
        prefix = "muyi.mq.product"
)
@Configuration
@Data
public class MyMqConsumerProperties {
    private String namesrvAddr;
    private String groupName;
}
