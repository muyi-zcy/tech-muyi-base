package tech.muyi.log;

import cn.hutool.core.util.RandomUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import tech.muyi.util.MyIdGenerator;

import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * description: LogInfoUtil
 * date: 2021/11/13 21:43
 * author: muyi
 * version: 1.0
 */
public class LogRecordUtil {
    /**
     * 获取traceId
     * 服务器 IP + ID 产生的时间 + 雪花算法 + 当前进程号
     *
     * @return
     */
    public static String getInitTraceId() {
        String ip = getWorkId();

        Thread t = Thread.currentThread();

        Long thread = t.getId();

        // 从ThreadLocal中获取当前线程的全链路线程ID
        return ip +
                "-" +
                System.currentTimeMillis() +
                "-" +
                MyIdGenerator.getNextId() +
                "-" +
                thread;
    }

    /**
     * 获取当前进程SpanId
     *
     * @return
     */
    public static String getSpanId() {
        return MDC.get(LogConstant.SPAN_ID);
    }

    /**
     * 获取当前进程TraceId
     *
     * @return
     */
    public static String getTraceId() {
        return MDC.get(LogConstant.TRACE_ID);
    }

    /**
     * 获取当前进程LogicId
     *
     * @return
     */
    public static String getLogicId() {
        return MDC.get(LogConstant.LOGIC_ID);
    }

    public static String getServiceNum() {
        return MDC.get(LogConstant.SERVICE_NUM);
    }

    /**
     * 获取传递给下游的LogicId
     *
     * @param serviceNum
     * @return
     */
    public static String incrLogicId(String serviceNum) {
        Integer serviceNumInt = StringUtils.isEmpty(serviceNum) ? 0 : Integer.parseInt(serviceNum);
        serviceNumInt++;
        MDC.put(LogConstant.SERVICE_NUM, serviceNumInt.toString());
        return serviceNumInt.toString();
    }

    /**
     * 生成新的spanId
     *
     * @param spanId  spanId
     * @param logicId logicId
     * @return spanId
     */
    public static String getNewSpanId(String spanId, String logicId) {
        return spanId + "." + logicId;
    }


    /**
     * 获取服务器ip
     *
     * @return 当前服务器IP
     */
    private static String getWorkId() {
        try {
            String hostAddress = Inet4Address.getLocalHost().getHostAddress();
            String[] ints = hostAddress.split("\\.");
            StringBuilder ipString = new StringBuilder();
            for (String i : ints) {
                ipString.append(Integer.toHexString(Integer.parseInt(i)));
            }
            return ipString.toString();
        } catch (UnknownHostException e) {
            return RandomUtil.randomString(8);
        }
    }
}
