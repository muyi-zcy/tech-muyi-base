package tech.muyi.util;

import cn.hutool.core.net.Ipv4Util;
import lombok.extern.slf4j.Slf4j;
import sun.net.util.IPAddressUtil;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * description: IpUtil
 *
 * <p>约定：
 * <ul>
 *   <li>{@link #getIpAddr(HttpServletRequest)} 优先读取代理头，适用于部署在网关/反向代理后的场景。</li>
 *   <li>{@link #getIP()} 返回当前机器首个可用 IPv4（非 loopback）。</li>
 * </ul>
 *
 * <p>安全提示：代理头可被伪造，生产环境应结合可信代理白名单或网关层清洗后再使用。</p>
 *
 * date: 2021/11/13 21:59
 * author: muyi
 * version: 1.0
 */
@Slf4j
public class IpUtil {
    private static final String LOCAL_IP = "127.0.0.1";

    /**
     * 获取IP地址
     * <p>
     * 使用Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址
     * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        // 按常见代理头优先级逐步回退，兼容不同中间件转发策略。
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // IPv6 loopback 统一折叠为 127.0.0.1，避免下游按字符串判断时出现分支。
        return "0:0:0:0:0:0:0:1".equals(ip) ? LOCAL_IP : ip;
    }


    public static String getIP() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress address = null;
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    address = addresses.nextElement();
                    if (!address.isLoopbackAddress() && !address.getHostAddress().contains(":")) {
                        // 仅返回 IPv4，便于与历史日志/配置保持一致。
                        return address.getHostAddress();
                    }
                }
            }
            return null;
        } catch (Exception e) {
            log.error("IpUtil#getIP", e);
            return null;
        }
    }
}