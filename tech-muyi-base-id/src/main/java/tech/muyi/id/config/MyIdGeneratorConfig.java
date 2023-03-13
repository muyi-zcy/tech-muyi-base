package tech.muyi.id.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import tech.muyi.id.MyIdGenerator;
import tech.muyi.id.constant.Constants;
import tech.muyi.id.properties.MyIdProperties;
import tech.muyi.id.properties.MyIdSnowflakeProperties;
import tech.muyi.id.snowflake.DefaultSnowflakeImpl;

/**
 * @author: muyi
 * @date: 2023/1/10
 **/

@Slf4j
@Configuration
@Import({MyIdProperties.class})
public class MyIdGeneratorConfig {

    @Autowired
    private MyIdProperties myIdProperties;

    @Bean(value = "Snowflake")
    @Primary
    public MyIdGenerator defaultSnowflakeImpl() {
        MyIdSnowflakeProperties myIdSnowflakeProperties = myIdProperties == null || myIdProperties.getSnowflake() == null ? new MyIdSnowflakeProperties() : myIdProperties.getSnowflake();
        if (myIdProperties == null || myIdProperties.getSnowflake() == null) {
            // 暂时只设置获取系统变量的方式获取，后续提供配置加载器，动态获取配置
            if (System.getProperty(Constants.WORKER_ID) != null) {
                myIdSnowflakeProperties.setWorkerId(Long.valueOf(System.getProperty(Constants.WORKER_ID)));
            }
            if (System.getProperty(Constants.DATACENTER_ID) != null) {
                myIdSnowflakeProperties.setDataCenterId(Long.valueOf(System.getProperty(Constants.DATACENTER_ID)));
            }
        }
        if (myIdSnowflakeProperties.getWorkerId() == null || myIdSnowflakeProperties.getDataCenterId() == null) {
            log.info("ID生成器无法获取自定义配置，使用默认配置！！！");
        }

        MyIdGenerator myIdGenerator = new DefaultSnowflakeImpl(myIdSnowflakeProperties);
        myIdGenerator.init();
        return myIdGenerator;
    }

}
