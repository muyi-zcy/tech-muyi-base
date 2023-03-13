package tech.muyi.config.system;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import tech.muyi.config.constant.Constants;
import tech.muyi.util.IpUtil;
import tech.muyi.util.constant.GlobalConstants;

/**
 *
 * @author: muyi
 * @date: 2022/11/26
 **/
@Configuration
@AutoConfigureOrder(value = Integer.MIN_VALUE)
public class InitConfiguration {
    @Autowired
    private Environment environment;

    @Bean
    public MyProperties initMyProperties() {
        MyProperties myProperties = new MyProperties();
        myProperties.setApp(environment.getProperty("spring.application.name"));

        String nodeId = System.getProperty(Constants.NODE_ID);
        if (StringUtils.isEmpty(nodeId)) {
            nodeId = myProperties.getApp() + "_temp_" + RandomUtils.nextInt();
        }
        myProperties.setNodeId(nodeId);


        return myProperties;
    }
}
