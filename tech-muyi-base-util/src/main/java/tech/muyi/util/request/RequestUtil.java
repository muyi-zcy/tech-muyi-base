package tech.muyi.util.request;


import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * HTTP 请求工具。
 *
 * <p>当前仅提供 Header 快照提取，用于日志透传、签名校验或链路诊断等场景。</p>
 *
 * <p>注意：返回的是普通 {@link HashMap}，键名大小写保持容器返回值，不做标准化。</p>
 *
 * @Author: muyi
 * @Date: 2021/1/26 19:42
 */
public class RequestUtil {
    public static Map<String, String> getHeaderMap(HttpServletRequest request) {
        Map<String, String> resultMap = new HashMap<>();
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            String value = request.getHeader(key);
            // 同名 header 仅保留 getHeader 的首值；多值聚合由上层按需处理。
            resultMap.put(key, value);
        }

        return resultMap;
    }
}
