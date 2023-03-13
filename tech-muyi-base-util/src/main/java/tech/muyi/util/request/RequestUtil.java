package tech.muyi.util.request;


import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
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
            resultMap.put(key, value);
        }

        return resultMap;
    }
}
