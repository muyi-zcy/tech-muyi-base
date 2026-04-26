package tech.muyi.config.system;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

/**
 * 基础系统属性载体。
 *
 * <p>由自动配置类在启动阶段构建并注册为 Bean，供其他模块统一读取：
 * <ul>
 *   <li>app：来自 `spring.application.name`</li>
 *   <li>nodeId：节点唯一标识，优先系统属性，缺省时使用临时值</li>
 * </ul>
 * </p>
 *
 * @author: muyi
 * @date: 2022/11/26
 **/
@Data
public class MyProperties {
    // 应用名称
    private String app;

    // 节点Id,集群部署的唯一标识
    private String nodeId;

}
