package tech.muyi.id.properties;

import lombok.Data;

/**
 * 雪花算法参数配置。
 *
 * <p>用于覆盖默认 epoch、节点标识与时钟策略，避免不同部署环境 ID 位段冲突。</p>
 *
 * @author: muyi
 * @date: 2023/1/10
 **/
@Data
public class MyIdSnowflakeProperties {
    // 起始时间（毫秒时间戳）
    private Long baseTime;
    private Long workerId;
    private Long dataCenterId;
    private boolean useSystemClock;
    private Long timeOffs;

}
