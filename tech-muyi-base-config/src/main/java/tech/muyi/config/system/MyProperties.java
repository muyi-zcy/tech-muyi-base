package tech.muyi.config.system;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

/**
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
