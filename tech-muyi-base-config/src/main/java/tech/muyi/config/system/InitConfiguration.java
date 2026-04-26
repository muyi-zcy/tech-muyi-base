package tech.muyi.config.system;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import tech.muyi.config.constant.Constants;

/**
 * 系统基础属性初始化自动配置。
 *
 * <p>{@link AutoConfigureOrder} 设置为最小值，确保在大多数自动配置前完成
 * `MyProperties` 注入，减少下游组件读取空值的概率。</p>
 *
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
        // 读取 Spring 标准应用名，作为实例标识前缀。
        myProperties.setApp(environment.getProperty("spring.application.name"));

        String nodeId = System.getProperty(Constants.NODE_ID);
        if (StringUtils.isEmpty(nodeId)) {
            // 未显式指定节点 ID 时生成临时值，保证单机场景也有可区分标识。
            nodeId = myProperties.getApp() + "_temp_" + RandomUtils.nextInt();
        }
        myProperties.setNodeId(nodeId);


        return myProperties;
    }
}
