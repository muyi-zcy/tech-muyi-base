package tech.muyi.id.properties;

import lombok.Data;

/**
 * @author: muyi
 * @date: 2023/1/10
 **/
@Data
public class MyIdSnowflakeProperties {
    //    起始时间
    private Long baseTime;
    private Long workerId;
    private Long dataCenterId;
    private boolean useSystemClock;
    private Long timeOffs;

}
