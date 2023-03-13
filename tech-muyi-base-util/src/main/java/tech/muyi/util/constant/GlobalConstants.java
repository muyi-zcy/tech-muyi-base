package tech.muyi.util.constant;

import cn.hutool.core.util.RuntimeUtil;
import tech.muyi.util.IpUtil;

/**
 * @author: muyi
 * @date: 2023/1/11
 **/
public class GlobalConstants {

    public static final String IP;

    public static final Integer PID;

    static {
        IP = IpUtil.getIP();
        PID = RuntimeUtil.getPid();

    }
}
