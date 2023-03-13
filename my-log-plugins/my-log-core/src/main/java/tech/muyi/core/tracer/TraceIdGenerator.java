package tech.muyi.core.tracer;

import tech.muyi.util.constant.GlobalConstants;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: muyi
 * @date: 2022/12/28
 **/
public class TraceIdGenerator {

    private static final String IP;
    private static final Integer PID;
    private static final AtomicInteger count = new AtomicInteger(1000);

    static {
        String ip = GlobalConstants.IP;
        if (ip == null) {
            IP = "ffffffff";
        } else {
            IP = getIP_16(ip);
        }

        PID = GlobalConstants.PID;
    }

    public static String getInitTraceId() {
        return getInitTraceId(IP, System.currentTimeMillis(), getNextId(), PID);
    }

    public static String getInitTraceId(String ip, long timestamp, int nextId, int PID) {
        return ip + "-" + timestamp + "-" + nextId + "-" + PID;
    }

    public static int getNextId() {
        for (; ; ) {
            int current = count.get();
            int next = (current > 9000) ? 1000 : current + 1;
            if (count.compareAndSet(current, next)) {
                return next;
            }
        }
    }

    private static String getIP_16(String ip) {
        String[] ips = ip.split("\\.");
        StringBuilder sb = new StringBuilder();
        for (String column : ips) {
            String hex = Integer.toHexString(Integer.parseInt(column));
            if (hex.length() == 1) {
                sb.append('0').append(hex);
            } else {
                sb.append(hex);
            }

        }
        return sb.toString();
    }
}
