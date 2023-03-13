package tech.muyi.core.constants;

/**
 * @author: muyi
 * @date: 2022/12/28
 **/
public enum TracerTypeEnum {
    SPRING_MVC("spring_mvc"),
    DUBBO_CLIENT("dubbo_client"),
    DUBBO_SERVER("dubbo_server"),
    DB("db"),
    REDIS("redis"),
    ROCKETMQ("rocketmq"),
    ASYNC("async"),
    ;

    private String code;

    TracerTypeEnum(String code){
        this.code = code;
    }

    public String getCode(){
        return this.code;
    }
}
