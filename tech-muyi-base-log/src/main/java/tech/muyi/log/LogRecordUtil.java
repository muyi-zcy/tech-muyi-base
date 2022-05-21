package tech.muyi.log;

import cn.hutool.core.util.RandomUtil;
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
     * @return
     */
    public static String getInitTraceId(){
        String ip = getWorkId();

        Thread t = Thread.currentThread();

        Long thread = t.getId();

        // 从ThreadLocal中获取当前线程的全链路线程ID
        StringBuilder stringBuilder  = new StringBuilder();
        stringBuilder.append(ip)
                .append("-")
                .append(System.currentTimeMillis())
                .append("-")
                .append(MyIdGenerator.getNextId())
                .append("-")
                .append(thread);
        return stringBuilder.toString();
    }

    /**
     * 获取当前进程SpanId
     * @return
     */
    public static String getSpanId(){
        return MDC.get(LogConstant.SPAN_ID);
    }

    /**
     * 获取当前进程TraceId
     * @return
     */
    public static String getTraceId(){
        return MDC.get(LogConstant.TRACE_ID);
    }

    /**
     * 获取当前进程LogicId
     * @return
     */
    public static String getLogicId(){
        return MDC.get(LogConstant.LOGIC_ID);
    }

    /**
     * 由上游节点获取当前服务节点LogicId
     * @param logicId
     * @return
     */
    public static String incrLogicId(String logicId) {
        return String.valueOf(Integer.parseInt(logicId) + 1);
    }

    /**
     * 生成新的spanId
     * @param spanId
     * @param logicId
     * @return
     */
    public static String getNewSpanId(String spanId, String logicId) {
        return new StringBuilder(spanId).append(".").append(logicId).toString();
    }


    /**
     * 获取服务器ip
     * @return
     */
    private static String getWorkId() {
        try {
            String hostAddress = Inet4Address.getLocalHost().getHostAddress();
            String[] ints = hostAddress.split("\\.");
            StringBuilder ipString = new StringBuilder();
            for (String i : ints) {
                ipString.append(Integer.toHexString(Integer.valueOf(i)));
            }
            return ipString.toString();
        } catch (UnknownHostException e) {
            return RandomUtil.randomString(8);
        }
    }
}
