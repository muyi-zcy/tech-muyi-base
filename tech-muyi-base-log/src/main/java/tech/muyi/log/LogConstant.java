package tech.muyi.log;

/**
 * description: LogConstant
 * date: 2021/11/13 16:47
 * author: muyi
 * version: 1.0
 */
public class LogConstant {
    public static final String TRACE_ID = "traceId";
    /**
     * 分段ID
     * 同一个调用链下的分段调用ID
     * 对于前端收到请求，生成的spanId固定都是0
     * 签名方式生成:0, 0.1, 0.1.1, 0.2
     */
    public static final String SPAN_ID = "spanId";

    public static final String LOGIC_ID = "logicId";

    public static final String IP_MDC = "requestIp";

    public static final String THREAD = "thread";

    public static final String SERVICE_NUM = "SERVICE_NUM";

    public static final String DEVICE_TYPE = "deviceType";

    public static final String GLOBAL_CONTEXT = "globalContext";

}
