package tech.muyi.util.constant;

import cn.hutool.core.util.RuntimeUtil;
import tech.muyi.util.IpUtil;

/**
 * 进程级只读常量。
 *
 * <p>在类加载时固定当前节点 IP 与进程 PID，适合用于日志标记、分布式诊断等场景。
 * 该值不会随网卡变化或容器网络重配实时刷新。</p>
 *
 * @author: muyi
 * @date: 2023/1/11
 **/
public class GlobalConstants {

    public static final String IP;

    public static final Integer PID;

    static {
        // 启动期快照，运行期直接复用，避免每次调用重新探测系统信息。
        IP = IpUtil.getIP();
        PID = RuntimeUtil.getPid();

    }
}
